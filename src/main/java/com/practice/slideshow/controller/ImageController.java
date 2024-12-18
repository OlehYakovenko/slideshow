package com.practice.slideshow.controller;

import com.practice.slideshow.dto.AddImageRequest;
import com.practice.slideshow.dto.ImageSearchResponse;
import com.practice.slideshow.entity.ImageEntity;
import com.practice.slideshow.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing image-related operations.
 */
@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ImageController {

  private final ImageService imageService;

  /**
   * Adds a new image with the provided URL and duration.
   *
   * @param request The request containing image details.
   * @return The ID of the newly added image.
   */
  @PostMapping()
  @ResponseStatus(HttpStatus.CREATED)
  public ImageEntity addImage(@RequestBody @Validated AddImageRequest request) {
    log.info("Received request to add an image with URL: {} and duration: {}", request.url(), request.duration());
    var image = imageService.addImage(request.url(), request.duration());
    log.info("Image added successfully with ID: {}", image.getId());
    return image;
  }

  /**
   * Deletes an image by its ID.
   *
   * @param id The ID of the image to delete.
   */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteImage(@PathVariable Long id) {
    log.info("Received request to delete image with ID: {}", id);
    imageService.deleteImage(id);
    log.info("Image deleted successfully for ID: {}", id);
  }

  /**
   * Searches for images by a given keyword and returns search results.
   *
   * @param keyword The keyword to search for.
   * @return A response containing the search results.
   */
  @GetMapping()
  public ImageSearchResponse search(@RequestParam String keyword) {
    log.info("Received request to search images with keyword: {}", keyword);
    ImageSearchResponse response = imageService.searchImagesWithResults(keyword);
    log.info("Search completed for keyword: {}, found {} results.", keyword, response.results().size());
    return response;
  }
}

