package com.practice.slideshow.service;

import com.practice.slideshow.mapper.ImageMapper;
import com.practice.slideshow.dto.ImageResponse;
import com.practice.slideshow.dto.ImageResult;
import com.practice.slideshow.dto.ImageSearchResponse;
import com.practice.slideshow.dto.LogEvent;
import com.practice.slideshow.dto.LogEventType;
import com.practice.slideshow.entity.ImageEntity;
import com.practice.slideshow.entity.SlideshowEntity;
import com.practice.slideshow.exception.ResourceNotFoundException;
import com.practice.slideshow.repository.ImageRepository;
import com.practice.slideshow.repository.SlideshowRepository;
import com.practice.slideshow.specification.ImageSpecification;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class responsible for managing image-related operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

  private final ImageRepository imageRepository;
  private final UrlValidationService urlValidationService;
  private final KafkaLoggingService kafkaLoggingService;
  private final SlideshowRepository slideshowRepository;


  /**
   * Adds a new image after validating its URL and duration.
   *
   * @param url      The image URL.
   * @param duration The duration for the image display.
   * @return The created ImageEntity.
   */
  @Transactional
  public ImageResponse addImage(String url, int duration) {
    urlValidationService.validateImageUrl(url);
    log.info("Adding new image with URL: {} and duration: {}", url, duration);

    ImageEntity savedImage = imageRepository.save(ImageEntity.builder()
        .url(url)
        .duration(duration)
        .build());

    kafkaLoggingService.logAction(new LogEvent(savedImage.getId(), LogEventType.ADD_IMAGE));
    log.info("Image added successfully with ID: {}", savedImage.getId());

    return ImageMapper.mapFromEntity(savedImage);
  }

  /**
   * Deletes an image by its ID.
   *
   * @param id The image ID.
   */
  public void deleteImage(Long id) {
    log.info("Attempting to delete image with ID: {}", id);
    ImageEntity image = imageRepository.findById(id)
        .orElseThrow(() -> {
          log.error("Image with ID: {} not found.", id);
          return new ResourceNotFoundException("Image not found, id: " + id);
        });
    kafkaLoggingService.logAction(new LogEvent(image.getId(), LogEventType.DELETE_IMAGE));
    imageRepository.delete(image);
    log.info("Image deleted successfully for ID: {}", id);
  }

  /**
   * Searches images by a keyword, matching the URL or duration.
   *
   * @param keyword The search keyword.
   * @return A list of matching images.
   */
  @Transactional(readOnly = true)
  public List<ImageEntity> searchImages(String keyword) {
    log.info("Searching for images with keyword: {}", keyword);
    return imageRepository.findAll(ImageSpecification.hasUrlContaining(keyword));
  }

  /**
   * Searches for images by a keyword and maps results to a response format.
   *
   * @param keyword The search keyword.
   * @return A response containing the list of matching images.
   */
  @Transactional(readOnly = true)
  public ImageSearchResponse searchImagesWithResults(String keyword) {
    log.info("Searching for images and mapping results with keyword: {}", keyword);

    var images = searchImages(keyword);
    var imageIds = images.stream().map(ImageEntity::getId).toList();

    var associatedSlideshowsMap = slideshowRepository.findAssociatedSlideshowsByImageIds(imageIds)
        .stream()
        .collect(Collectors.groupingBy(
            SlideshowRepository.AssociatedSlideshowProjection::getImageId,
            Collectors.mapping(SlideshowRepository.AssociatedSlideshowProjection::getSlideshowId,
                Collectors.toList())
        ));

    var results = images.stream().map(i -> ImageResult.builder()
        .imageId(i.getId())
        .url(i.getUrl())
        .duration(i.getDuration())
        .associatedSlideshows(
            associatedSlideshowsMap.getOrDefault(i.getId(), Collections.emptyList()))
        .build()).toList();

    log.info("Search completed. Found {} results for keyword: {}", results.size(), keyword);
    return ImageSearchResponse.builder().results(results).build();
  }
}
