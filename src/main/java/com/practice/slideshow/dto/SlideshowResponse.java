package com.practice.slideshow.dto;

import com.practice.slideshow.entity.SlideshowEntity;
import lombok.Builder;

@Builder
public record SlideshowResponse(Long id, String createdAt) {
  public static SlideshowResponse fromEntity(SlideshowEntity entity) {
    return SlideshowResponse.builder()
        .id(entity.getId())
        .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)
        .build();
  }
}
