package com.practice.slideshow.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.slideshow.dto.AddSlideshowRequest;
import com.practice.slideshow.dto.ImageData;
import com.practice.slideshow.dto.SlideshowImageInput;
import com.practice.slideshow.dto.SlideshowImageOrderResponse;
import com.practice.slideshow.service.SlideshowService;
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
  MockMvc mockMvc;

  @MockBean
  SlideshowService slideshowService;

  @Autowired
  ObjectMapper objectMapper;

  @Test
  void addSlideshow_ShouldReturnCreatedAndSlideshowId() throws Exception {
    AddSlideshowRequest request = new AddSlideshowRequest(
        List.of(new SlideshowImageInput("https://example.com/img.jpg", 10, 1))
    );

    var slideshow = com.practice.slideshow.entity.SlideshowEntity.builder().id(1L).build();
    Mockito.when(slideshowService.addSlideshow(Mockito.any())).thenReturn(slideshow);

    mockMvc.perform(post("/slideshow")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(content().string("1"));
  }

  @Test
  void deleteSlideshow_ShouldReturnNoContent() throws Exception {
    Long slideshowId = 5L;
    Mockito.doNothing().when(slideshowService).deleteSlideshow(slideshowId);

    mockMvc.perform(delete("/slideshow/{id}", slideshowId))
        .andExpect(status().isNoContent());
  }

  @Test
  void getSlideshowOrder_ShouldReturnImages() throws Exception {
    Long slideshowId = 2L;

    var response = SlideshowImageOrderResponse.builder()
        .slideshowId(slideshowId)
        .images(List.of(
            ImageData.builder().imageId(100L).url("https://example.com/img.jpg").duration(10).position(1).addedAt("2023-01-01T00:00:00").build()
        )).build();

    Mockito.when(slideshowService.getSlideshowOrder(slideshowId)).thenReturn(response);

    mockMvc.perform(get("/slideshow/{id}/slideshowOrder", slideshowId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.slideshowId").value(2L))
        .andExpect(jsonPath("$.images[0].imageId").value(100L));
  }

  @Test
  void proofOfPlay_ShouldReturnOk() throws Exception {
    Long slideshowId = 3L;
    Long imageId = 10L;
    Mockito.doNothing().when(slideshowService).recordProofOfPlay(slideshowId, imageId);

    mockMvc.perform(post("/slideshow/{id}/proof-of-play/{imageId}", slideshowId, imageId))
        .andExpect(status().isOk());
  }
}