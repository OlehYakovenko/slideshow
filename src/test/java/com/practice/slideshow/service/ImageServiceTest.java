package com.practice.slideshow.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.practice.slideshow.dto.ImageResult;
import com.practice.slideshow.dto.ImageSearchResponse;
import com.practice.slideshow.entity.ImageEntity;
import com.practice.slideshow.exception.ResourceNotFoundException;
import com.practice.slideshow.repository.ImageRepository;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

  @Mock
  private ImageRepository imageRepository;

  @Mock
  private UrlValidationService urlValidationService;

  @Mock
  private KafkaLoggingService kafkaLoggingService;

  @InjectMocks
  private ImageService imageService;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void addImage_ShouldAddAndLogImage() {
    String url = "https://example.com/img.jpg";
    int duration = 10;
    ImageEntity savedImage = ImageEntity.builder().id(1L).url(url).duration(duration).build();

    doNothing().when(urlValidationService).validateImageUrl(url);
    when(imageRepository.save(any(ImageEntity.class))).thenReturn(savedImage);

    doNothing().when(kafkaLoggingService).logAction(eq("ADD_IMAGE"), anyString());

    ImageEntity result = imageService.addImage(url, duration);

    assertNotNull(result.getId());
    assertEquals(1L, result.getId());
    verify(urlValidationService).validateImageUrl(url);
    verify(kafkaLoggingService).logAction(eq("ADD_IMAGE"),
        contains("Image ID: 1, URL: https://example.com/img.jpg"));
  }

  @Test
  void deleteImage_ShouldDeleteIfExists() {
    Long imageId = 10L;
    when(imageRepository.existsById(imageId)).thenReturn(true);

    doNothing().when(kafkaLoggingService).logAction(eq("DELETE_IMAGE"), anyString());

    imageService.deleteImage(imageId);

    verify(imageRepository).deleteById(imageId);
    verify(kafkaLoggingService).logAction(eq("DELETE_IMAGE"), contains("Deleted Image ID: 10"));
  }

  @Test
  void deleteImage_ShouldThrowIfNotExists() {
    Long imageId = 99L;
    when(imageRepository.existsById(imageId)).thenReturn(false);

    assertThrows(ResourceNotFoundException.class, () -> imageService.deleteImage(imageId));

    verify(kafkaLoggingService, never()).logAction(eq("DELETE_IMAGE"), anyString());
  }

  @Test
  void searchImagesWithResults_ShouldReturnResponse() {
    String keyword = "test";
    ImageEntity image = ImageEntity.builder().id(2L).url("https://example.com/t.jpg").duration(5)
        .build();

    when(imageRepository.findAll(any(Example.class))).thenReturn(Collections.singletonList(image));

    ImageSearchResponse response = imageService.searchImagesWithResults(keyword);

    assertEquals(1, response.results().size());
    ImageResult result = response.results().get(0);
    assertEquals(2L, result.imageId());
    assertEquals("https://example.com/t.jpg", result.url());
    assertEquals(5, result.duration());
    assertTrue(result.associatedSlideshows().isEmpty());
  }

  @Test
  void searchImagesWithResults_ShouldReturnEmptyIfNoResults() {
    String keyword = "noresult";

    when(imageRepository.findAll(any(Example.class))).thenReturn(Collections.emptyList());

    ImageSearchResponse response = imageService.searchImagesWithResults(keyword);

    assertNotNull(response);
    assertTrue(response.results().isEmpty());
  }
}
