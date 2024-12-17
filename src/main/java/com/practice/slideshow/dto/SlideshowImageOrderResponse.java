package com.practice.slideshow.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record SlideshowImageOrderResponse(
    Long slideshowId,
    List<ImageData> images
) {
  @Builder
  public record ImageData(Long imageId, String url, int duration, String addedAt, int position) {}
}
