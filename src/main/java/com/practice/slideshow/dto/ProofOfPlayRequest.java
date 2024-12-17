package com.practice.slideshow.dto;

import jakarta.validation.constraints.NotNull;

public record ProofOfPlayRequest(
    @NotNull Long nextImageId
) {}
