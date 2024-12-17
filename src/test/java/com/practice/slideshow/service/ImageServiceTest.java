package com.practice.slideshow.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.practice.slideshow.entity.ImageEntity;
import com.practice.slideshow.exception.ResourceNotFoundException;
import com.practice.slideshow.repository.ImageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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

  @Test
  void shouldAddImageSuccessfully() {
    // Given
    String url = "https://example.com/image.jpg";
    int duration = 5;
    ImageEntity savedImageEntity = ImageEntity.builder()
        .id(1L)
        .url(url)
        .duration(duration)
        .build();
    given(imageRepository.save(Mockito.any(ImageEntity.class))).willReturn(savedImageEntity);

    // When
    ImageEntity result = imageService.addImage(url, duration);

    // Then
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getUrl()).isEqualTo(url);
    assertThat(result.getDuration()).isEqualTo(duration);

    verify(imageRepository).save(Mockito.any(ImageEntity.class));
    verify(kafkaLoggingService).logAction("ADD_IMAGE",
        "Image ID: 1, URL: https://example.com/image.jpg");
  }

  @Test
  void shouldThrowWhenImageNotFoundOnDelete() {
    // Given
    Long imageId = 1L;
    given(imageRepository.existsById(imageId)).willReturn(false);

    // When & Then
    assertThrows(ResourceNotFoundException.class, () -> imageService.deleteImage(imageId));
  }

  @Test
  void shouldDeleteImageSuccessfully() {
    // Given
    Long imageId = 1L;
    given(imageRepository.existsById(imageId)).willReturn(true);

    // When
    imageService.deleteImage(imageId);

    // Then
    verify(imageRepository).deleteById(imageId);
    verify(kafkaLoggingService).logAction("DELETE_IMAGE", "Deleted Image ID: 1");
  }
}
