package com.practice.slideshow.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record ImageResult(Long imageId, String url, Integer duration, List<Long> associatedSlideshows) {

}
