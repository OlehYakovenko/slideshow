package com.practice.slideshow.dto;

import com.practice.slideshow.entity.ImageEntity;
import lombok.Builder;

@Builder
public record ImageResponse(Long imageId, String url, int duration, String addedAt) {
  public static ImageResponse fromEntity(ImageEntity entity) {
    return ImageResponse.builder()
        .imageId(entity.getId())
        .url(entity.getUrl())
        .duration(entity.getDuration() != null ? entity.getDuration() : 0)
        .addedAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)
        .build();
  }
}
