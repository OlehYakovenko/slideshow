package com.practice.slideshow.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.slideshow.dto.AddImageRequest;
import com.practice.slideshow.entity.ImageEntity;
import com.practice.slideshow.service.ImageService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ImageController.class)
class ImageControllerTest {
  private final Long imageId = 1L;

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ImageService imageService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void shouldAddImage() throws Exception {
    // Given
    AddImageRequest request = new AddImageRequest("https://example.com/image.jpg", 5);
    given(imageService.addImage(request.url(), request.duration()))
        .willReturn(ImageEntity.builder().id(imageId).url(request.url())
            .duration(request.duration()).build());

    // When & Then
    mockMvc.perform(post("/images/addImage")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());
  }

  @Test
  void shouldDeleteImage() throws Exception {
    // Given
    Mockito.doNothing().when(imageService).deleteImage(imageId);

    // When & Then
    mockMvc.perform(delete("/images/deleteImage/{id}", imageId))
        .andExpect(status().isNoContent());
  }

  @Test
  void shouldSearchImages() throws Exception {
    // Given
    String keyword = "image";
    given(imageService.searchImages(keyword)).willReturn(List.of());

    // When & Then
    mockMvc.perform(get("/images/search")
            .param("keyword", keyword))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.results", hasSize(0)));
  }
}