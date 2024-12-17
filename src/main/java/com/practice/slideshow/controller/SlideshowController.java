package com.practice.slideshow.controller;

import com.practice.slideshow.dto.AddSlideshowRequest;
import com.practice.slideshow.dto.ProofOfPlayRequest;
import com.practice.slideshow.dto.SlideshowImageOrderResponse;
import com.practice.slideshow.entity.SlideshowEntity;
import com.practice.slideshow.entity.SlideshowImageLink;
import com.practice.slideshow.exception.ResourceNotFoundException;
import com.practice.slideshow.service.SlideshowService;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/slideShow")
@RequiredArgsConstructor
public class SlideshowController {

  private final SlideshowService slideshowService;

  @PostMapping("/addSlideshow")
  @ResponseStatus(HttpStatus.CREATED)
  public Long addSlideshow(@RequestBody AddSlideshowRequest request) {
    var imageDataList = request.images().stream()
        .map(s -> new SlideshowService.SlideshowImageData(s.url(), s.duration(), s.position()))
        .toList();
    var slideshow = slideshowService.addSlideshow(imageDataList);
    return slideshow.getId();
  }

  @DeleteMapping("/deleteSlideshow/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteSlideshow(@PathVariable Long id) {
    slideshowService.deleteSlideshow(id);
  }

  @GetMapping("/{id}/slideshowOrder")
  public SlideshowImageOrderResponse getSlideshowOrder(@PathVariable Long id) {
    SlideshowEntity slideshow = slideshowService.findById(id);
    if (slideshow == null) {
      throw new ResourceNotFoundException("Slideshow not found for ID: " + id);
    }

    var images = slideshow.getSlideshowImages().stream()
        .map(link -> SlideshowImageOrderResponse.ImageData.builder()
            .imageId(link.getImage().getId())
            .url(link.getImage().getUrl())
            .duration(link.getImage().getDuration())
            .addedAt(link.getImage().getCreatedAt().toString())
            .position(link.getPosition())
            .build())
        .toList();

    return SlideshowImageOrderResponse.builder()
        .slideshowId(slideshow.getId())
        .images(images)
        .build();
  }

  @PostMapping("/{id}/proof-of-play/{imageId}")
  @ResponseStatus(HttpStatus.OK)
  public void proofOfPlay(@PathVariable Long id,
      @PathVariable Long imageId) {
    slideshowService.recordProofOfPlay(id, imageId);
  }
}
