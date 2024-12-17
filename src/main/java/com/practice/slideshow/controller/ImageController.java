package com.practice.slideshow.controller;

import com.practice.slideshow.dto.AddImageRequest;
import com.practice.slideshow.dto.ImageSearchResponse;
import com.practice.slideshow.service.ImageService;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
@Validated
public class ImageController {

  private final ImageService imageService;

  @PostMapping("/addImage")
  @ResponseStatus(HttpStatus.CREATED)
  public Long addImage(@RequestBody @Validated AddImageRequest request) {
    var image = imageService.addImage(request.url(), request.duration());
    return image.getId();
  }

  @DeleteMapping("/deleteImage/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteImage(@PathVariable Long id) {
    imageService.deleteImage(id);
  }

  @GetMapping("/search")
  public ImageSearchResponse search(@RequestParam String keyword) {
    var images = imageService.searchImages(keyword);
    var results = images.stream().map(i ->
        ImageSearchResponse.ImageResult.builder()
            .imageId(i.getId())
            .url(i.getUrl())
            .duration(i.getDuration())
            .associatedSlideshows(Collections.emptyList())
            .build()
    ).toList();
    return ImageSearchResponse.builder().results(results).build();
  }
}

