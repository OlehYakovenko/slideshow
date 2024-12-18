package com.practice.slideshow.service;

import com.practice.slideshow.dto.ImageData;
import com.practice.slideshow.dto.SlideshowImageData;
import com.practice.slideshow.dto.SlideshowImageOrderResponse;
import com.practice.slideshow.entity.SlideshowEntity;
import com.practice.slideshow.entity.SlideshowImageId;
import com.practice.slideshow.entity.SlideshowImageLink;
import com.practice.slideshow.exception.ResourceNotFoundException;
import com.practice.slideshow.repository.SlideshowRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class responsible for managing slideshow operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SlideshowService {

  private final SlideshowRepository slideshowRepository;
  private final ProofOfPlayService proofOfPlayService;
  private final ImageService imageService;

  /**
   * Adds a new slideshow with the provided image data.
   *
   * @param imageDataList The list of image data for the slideshow.
   * @return The created slideshow entity.
   */
  @Transactional
  public SlideshowEntity addSlideshow(List<SlideshowImageData> imageDataList) {
    log.info("Adding a new slideshow with {} images.", imageDataList.size());

    SlideshowEntity slideshow = SlideshowEntity.builder().build();
    slideshowRepository.save(slideshow);

    var links = imageDataList.stream().map(data -> {
      var image = imageService.addImage(data.url(), data.duration());
      return SlideshowImageLink.builder()
          .id(new SlideshowImageId(slideshow.getId(), image.getId()))
          .slideshow(slideshow)
          .image(image)
          .position(data.position())
          .build();
    }).toList();

    slideshow.getSlideshowImages().addAll(links);
    log.info("Slideshow created successfully with ID: {}", slideshow.getId());
    return slideshowRepository.save(slideshow);
  }

  /**
   * Deletes a slideshow by its ID.
   *
   * @param id The ID of the slideshow to delete.
   * @throws ResourceNotFoundException If the slideshow does not exist.
   */
  @Transactional
  public void deleteSlideshow(Long id) {
    log.info("Deleting slideshow with ID: {}", id);

    if (!slideshowRepository.existsById(id)) {
      log.error("Slideshow with ID: {} not found.", id);
      throw new ResourceNotFoundException("Slideshow not found: id = " + id);
    }

    slideshowRepository.deleteById(id);
    log.info("Slideshow deleted successfully for ID: {}", id);
  }

  /**
   * Finds a slideshow by its ID.
   *
   * @param id The ID of the slideshow.
   * @return The found slideshow entity.
   * @throws ResourceNotFoundException If the slideshow does not exist.
   */
  @Transactional(readOnly = true)
  public SlideshowEntity findById(Long id) {
    log.info("Finding slideshow with ID: {}", id);
    return slideshowRepository.findById(id)
        .orElseThrow(() -> {
          log.error("Slideshow not found for ID: {}", id);
          return new ResourceNotFoundException("Slideshow not found for ID: " + id);
        });
  }

  /**
   * Records proof-of-play for a specific image in a slideshow.
   *
   * @param slideshowId    The ID of the slideshow.
   * @param currentImageId The ID of the current image.
   */
  @Transactional
  public void recordProofOfPlay(Long slideshowId, Long currentImageId) {
    log.info("Recording proof-of-play for slideshow ID: {} and image ID: {}", slideshowId, currentImageId);
    proofOfPlayService.record(slideshowId, currentImageId);
  }

  /**
   * Retrieves the order of images in a slideshow by its ID.
   *
   * @param id The ID of the slideshow.
   * @return A response containing the ordered images.
   */
  @Transactional(readOnly = true)
  public SlideshowImageOrderResponse getSlideshowOrder(Long id) {
    log.info("Retrieving slideshow order for ID: {}", id);

    SlideshowEntity slideshow = findById(id);

    var images = slideshow.getSlideshowImages().stream()
        .map(link -> ImageData.builder()
            .imageId(link.getImage().getId())
            .url(link.getImage().getUrl())
            .duration(link.getImage().getDuration())
            .addedAt(link.getImage().getCreatedAt().toString())
            .position(link.getPosition())
            .build())
        .toList();

    log.info("Slideshow order retrieved successfully for ID: {}", id);
    return SlideshowImageOrderResponse.builder()
        .slideshowId(slideshow.getId())
        .images(images)
        .build();
  }
}
