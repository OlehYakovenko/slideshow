package com.practice.slideshow.dto;

import java.util.List;

public record AddSlideshowRequest(
   List<SlideshowImageInput> images
) {
}
