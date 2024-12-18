package com.practice.slideshow.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.slideshow.dto.AddImageRequest;
import com.practice.slideshow.dto.ImageResult;
import com.practice.slideshow.dto.ImageSearchResponse;
import com.practice.slideshow.service.ImageService;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ImageController.class)
class ImageControllerTest {

  @Autowired
  MockMvc mockMvc;

  @MockBean
  ImageService imageService;

  @Autowired
  ObjectMapper objectMapper;

  @Test
  void addImage_ShouldReturnCreatedAndImageId() throws Exception {
    // Given
    AddImageRequest request = new AddImageRequest("https://example.com/image.jpg", 10);
    var image = com.practice.slideshow.entity.ImageEntity.builder().id(1L).url(request.url()).duration(request.duration()).build();
    Mockito.when(imageService.addImage(request.url(), request.duration())).thenReturn(image);

    // When & Then
    mockMvc.perform(post("/image")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.url").value("https://example.com/image.jpg"))
        .andExpect(jsonPath("$.duration").value(10));
  }

  @Test
  void deleteImage_ShouldReturnNoContent() throws Exception {
    Long imageId = 10L;
    Mockito.doNothing().when(imageService).deleteImage(imageId);

    mockMvc.perform(delete("/image/{id}", imageId))
        .andExpect(status().isNoContent());
  }

  @Test
  void searchImages_ShouldReturnResults() throws Exception {
    String keyword = "test";
    var response = ImageSearchResponse.builder()
        .results(Collections.singletonList(
            ImageResult.builder().imageId(100L).url("https://example.com/img.jpg").duration(5).associatedSlideshows(Collections.emptyList()).build()
        )).build();

    Mockito.when(imageService.searchImagesWithResults(anyString())).thenReturn(response);

    mockMvc.perform(get("/image")
            .param("keyword", keyword))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.results[0].imageId").value(100L));
  }
}