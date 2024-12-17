package com.practice.slideshow.service;

import com.practice.slideshow.entity.ImageEntity;
import com.practice.slideshow.exception.ResourceNotFoundException;
import com.practice.slideshow.repository.ImageRepository;
import com.practice.slideshow.specification.ImageSpecification;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing images.
 */
@Service
@RequiredArgsConstructor
public class ImageService {

  private final ImageRepository imageRepository;
  private final UrlValidationService urlValidationService;
  private final KafkaLoggingService kafkaLoggingService;

  /**
   * Adds a new image after validating its URL and duration.
   *
   * @param url      The image URL.
   * @param duration The duration for the image display.
   * @return The created ImageEntity.
   */
  @Transactional
  public ImageEntity addImage(String url, int duration) {
    urlValidationService.validateImageUrl(url);

    ImageEntity image = ImageEntity.builder()
        .url(url)
        .duration(duration)
        .build();

    ImageEntity savedImage = imageRepository.save(image);
    kafkaLoggingService.logAction("ADD_IMAGE",
        String.format("Image ID: %d, URL: %s", savedImage.getId(), url));

    return savedImage;
  }

  /**
   * Deletes an image by its ID.
   *
   * @param id The image ID.
   */
  @Transactional
  public void deleteImage(Long id) {
    if (!imageRepository.existsById(id)) {
      throw new ResourceNotFoundException("Image not found, id: " + id);
    }
    imageRepository.deleteById(id);
    kafkaLoggingService.logAction("DELETE_IMAGE", String.format("Deleted Image ID: %d", id));
  }

  /**
   * Searches images by a keyword, matching the URL or duration.
   * @param keyword search keyword
   * @return list of matching images
   */
  @Transactional(readOnly = true)
  public List<ImageEntity> searchImages(String keyword) {
    return imageRepository.findAll(ImageSpecification.hasUrlContaining(keyword));
  }
}
