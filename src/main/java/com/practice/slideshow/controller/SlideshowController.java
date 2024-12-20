package com.practice.slideshow.controller;

import com.practice.slideshow.dto.AddSlideshowRequest;
import com.practice.slideshow.dto.SlideshowImageOrderResponse;
import com.practice.slideshow.dto.SlideshowResponse;
import com.practice.slideshow.service.SlideshowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing slideshow-related operations.
 */
@RestController
@RequestMapping("/slideshow")
@RequiredArgsConstructor
@Slf4j
public class SlideshowController {

  private final SlideshowService slideshowService;

  /**
   * Adds a new slideshow with the provided images and durations.
   *
   * @param request The request containing slideshow details.
   * @return The ID of the newly created slideshow.
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public SlideshowResponse addSlideshow(@RequestBody AddSlideshowRequest request) {
    log.info("Received request to add a slideshow with {} images.", request.images().size());
    var slideshow = slideshowService.addSlideshow(request.images());
    log.info("Slideshow added successfully with ID: {}", slideshow.id());
    return slideshow;
  }

  /**
   * Deletes a slideshow by its ID.
   *
   * @param id The ID of the slideshow to delete.
   */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteSlideshow(@PathVariable Long id) {
    log.info("Received request to delete slideshow with ID: {}", id);

    slideshowService.deleteSlideshow(id);

    log.info("Slideshow deleted successfully for ID: {}", id);
  }

  /**
   * Retrieves the order of images in a slideshow by its ID.
   *
   * @param id The ID of the slideshow.
   * @return A response containing the ordered images.
   */
  @GetMapping("/{id}/slideshowOrder")
  public SlideshowImageOrderResponse getSlideshowOrder(@PathVariable Long id) {
    log.info("Received request to get slideshow order for ID: {}", id);

    SlideshowImageOrderResponse response = slideshowService.getSlideshowOrder(id);

    log.info("Slideshow order retrieved successfully for ID: {}", id);
    return response;
  }

  /**
   * Records proof-of-play for a specific image in a slideshow.
   *
   * @param id      The ID of the slideshow.
   * @param imageId The ID of the image.
   */
  @PostMapping("/{id}/proof-of-play/{imageId}")
  @ResponseStatus(HttpStatus.OK)
  public void proofOfPlay(@PathVariable Long id, @PathVariable Long imageId) {
    log.info("Received request to record proof-of-play for slideshow ID: {} and image ID: {}", id,
        imageId);

    slideshowService.recordProofOfPlay(id, imageId);

    log.info("Proof-of-play recorded successfully for slideshow ID: {} and image ID: {}", id,
        imageId);
  }
}
