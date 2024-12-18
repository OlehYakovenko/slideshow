package com.practice.slideshow.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record ImageSearchResponse(List<ImageResult> results) {

}
