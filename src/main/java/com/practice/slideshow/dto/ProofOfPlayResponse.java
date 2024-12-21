package com.practice.slideshow.dto;

import com.practice.slideshow.entity.ProofOfPlayEntity;
import lombok.Builder;

@Builder
public record ProofOfPlayResponse(Long id, Long slideshowId, Long imageId) {

  public static ProofOfPlayResponse mapFromEntity(ProofOfPlayEntity entity) {
    return ProofOfPlayResponse.builder()
        .id(entity.getId())
        .slideshowId(entity.getSlideshowId())
        .imageId(entity.getImageId())
        .build();
  }
}
