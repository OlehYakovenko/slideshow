package com.practice.slideshow.dto;

import lombok.Builder;

@Builder
public record LogEvent(Long id, LogEventType eventType) {

}
