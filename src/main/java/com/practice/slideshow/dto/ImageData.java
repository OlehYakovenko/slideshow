package com.practice.slideshow.dto;

import lombok.Builder;

@Builder
public record ImageData(Long imageId, String url, int duration, String addedAt, int position) {

}
