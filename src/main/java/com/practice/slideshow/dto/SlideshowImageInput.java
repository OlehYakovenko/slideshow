package com.practice.slideshow.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record SlideshowImageInput(@NotBlank String url,
                                  @Min(1) int duration,
                                  @Min(1) int position) {

}
