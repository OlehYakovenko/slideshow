package com.practice.slideshow.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.slideshow.dto.AddSlideshowRequest;
import com.practice.slideshow.dto.ProofOfPlayRequest;
import com.practice.slideshow.entity.ImageEntity;
import com.practice.slideshow.entity.SlideshowEntity;
import com.practice.slideshow.entity.SlideshowImageId;
import com.practice.slideshow.entity.SlideshowImageLink;
import com.practice.slideshow.service.SlideshowService;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(SlideshowController.class)
class SlideshowControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private SlideshowService slideshowService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void shouldAddSlideshow() throws Exception {
    // Given
    AddSlideshowRequest request = new AddSlideshowRequest(
        List.of(new AddSlideshowRequest.SlideshowImageInput(
            "https://example.com/image1.jpg", 5, 1))
    );

    Long slideshowId = 1L;
    SlideshowEntity slideshowEntity = SlideshowEntity.builder()
        .id(slideshowId)
        .build();

    given(slideshowService.addSlideshow(Mockito.any()))
        .willReturn(slideshowEntity);

    // When & Then
    mockMvc.perform(post("/slideShow/addSlideshow")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$").value(slideshowId));
  }

  @Test
  void shouldDeleteSlideshow() throws Exception {
    // Given
    Long slideshowId = 1L;
    Mockito.doNothing().when(slideshowService).deleteSlideshow(slideshowId);

    // When & Then
    mockMvc.perform(delete("/slideShow/deleteSlideshow/{id}", slideshowId))
        .andExpect(status().isNoContent());
  }

  @Test
  void shouldGetSlideshowOrder() throws Exception {
    // Given
    Long slideshowId = 1L;

    // Mock ImageEntity
    ImageEntity image = ImageEntity.builder()
        .id(1L)
        .url("https://example.com/image1.jpg")
        .duration(5)
        .createdAt(LocalDateTime.of(2024, 12, 16, 12, 34, 56))
        .build();

    // Mock SlideshowImageLink
    SlideshowImageLink link = SlideshowImageLink.builder()
        .id(new SlideshowImageId(slideshowId, image.getId()))
        .image(image)
        .position(1)
        .build();

    SlideshowEntity slideshow = SlideshowEntity.builder()
        .id(slideshowId)
        .slideshowImages(Collections.singletonList(link))
        .build();

    given(slideshowService.findById(slideshowId)).willReturn(slideshow);

    // When & Then
    mockMvc.perform(get("/slideShow/{id}/slideshowOrder", slideshowId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.slideshowId").value(slideshowId))
        .andExpect(jsonPath("$.images[0].imageId").value(1L))
        .andExpect(jsonPath("$.images[0].url").value("https://example.com/image1.jpg"))
        .andExpect(jsonPath("$.images[0].duration").value(5))
        .andExpect(jsonPath("$.images[0].addedAt").value("2024-12-16T12:34:56"))
        .andExpect(jsonPath("$.images[0].position").value(1));
  }

  @Test
  void shouldRecordProofOfPlay() throws Exception {
    // Given
    Long slideshowId = 1L;
    Long imageId = 1L;
    ProofOfPlayRequest request = new ProofOfPlayRequest(2L);

    Mockito.doNothing().when(slideshowService)
        .recordProofOfPlay(slideshowId, imageId);

    // When & Then
    mockMvc.perform(post("/slideShow/{id}/proof-of-play/{imageId}", slideshowId, imageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }
}
