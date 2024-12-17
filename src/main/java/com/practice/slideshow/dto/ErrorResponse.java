package com.practice.slideshow.dto;

import lombok.Builder;

@Builder
public record ErrorResponse(String errorCode, String message) {}
