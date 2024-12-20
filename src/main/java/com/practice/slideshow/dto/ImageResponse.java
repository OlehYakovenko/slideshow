package com.practice.slideshow.dto;

import lombok.Builder;

@Builder
public record ImageResponse(Long imageId, String url, int duration, String addedAt) {

}
