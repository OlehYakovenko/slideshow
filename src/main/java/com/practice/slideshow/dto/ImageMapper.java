package com.practice.slideshow.dto;

import com.practice.slideshow.entity.ImageEntity;
import java.time.LocalDateTime;

public class ImageMapper {
  public static ImageEntity mapToEntity(ImageResponse response) {
    return ImageEntity.builder()
        .id(response.imageId())
        .url(response.url())
        .duration(response.duration())
        .createdAt(response.addedAt() != null ? LocalDateTime.parse(response.addedAt()) : null)
        .build();
  }

  public static ImageResponse mapFromEntity(ImageEntity entity) {
    return ImageResponse.builder()
        .imageId(entity.getId())
        .url(entity.getUrl())
        .duration(entity.getDuration() != null ? entity.getDuration() : 0)
        .addedAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)
        .build();
  }
}
