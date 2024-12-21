package com.practice.slideshow.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.practice.slideshow.dto.ImageResponse;
import com.practice.slideshow.dto.ImageResult;
import com.practice.slideshow.dto.ImageSearchResponse;
import com.practice.slideshow.dto.LogEvent;
import com.practice.slideshow.dto.LogEventType;
import com.practice.slideshow.entity.ImageEntity;
import com.practice.slideshow.entity.SlideshowEntity;
import com.practice.slideshow.exception.ResourceNotFoundException;
import com.practice.slideshow.repository.ImageRepository;
import com.practice.slideshow.repository.SlideshowRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

  @Mock
  private ImageRepository imageRepository;

  @Mock
  private SlideshowRepository slideshowRepository;

  @Mock
  private UrlValidationService urlValidationService;

  @Mock
  private KafkaLoggingService kafkaLoggingService;

  @InjectMocks
  private ImageService imageService;


  @Test
  void addImage_ShouldAddAndLogImage() {
    String url = "https://example.com/img.jpg";
    int duration = 10;
    ImageEntity savedImage = ImageEntity.builder().id(1L).url(url).duration(duration).build();

    doNothing().when(urlValidationService).validateImageUrl(url);
    when(imageRepository.save(any(ImageEntity.class))).thenReturn(savedImage);

    doNothing().when(kafkaLoggingService).logAction(new LogEvent(1L, LogEventType.ADD_IMAGE));

    ImageResponse result = imageService.addImage(url, duration);

    assertNotNull(result.imageId());
    assertEquals(1L, result.imageId());
    verify(urlValidationService).validateImageUrl(url);
    verify(kafkaLoggingService).logAction(new LogEvent(1L, LogEventType.ADD_IMAGE));
  }

  @Test
  void deleteImage_ShouldDeleteIfExists() {
    // Given
    Long imageId = 10L;
    ImageEntity imageEntity = ImageEntity.builder().id(imageId).url("https://example.com/image.jpg")
        .build();

    when(imageRepository.findById(imageId)).thenReturn(Optional.of(imageEntity));
    doNothing().when(kafkaLoggingService)
        .logAction(new LogEvent(imageId, LogEventType.DELETE_IMAGE));

    // When
    imageService.deleteImage(imageId);

    // Then
    verify(imageRepository).delete(imageEntity);
    verify(kafkaLoggingService).logAction(new LogEvent(imageId, LogEventType.DELETE_IMAGE));
  }

  @Test
  void deleteImage_ShouldThrowIfNotExists() {
    // Given
    Long imageId = 99L;
    when(imageRepository.findById(imageId)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(ResourceNotFoundException.class, () -> imageService.deleteImage(imageId));
    verify(kafkaLoggingService, never()).logAction(any(LogEvent.class));
  }

  @Test
  void searchImagesWithResults_ShouldReturnResponse() {
    // Given
    String keyword = "test";

    ImageEntity image = ImageEntity.builder()
        .id(2L)
        .url("https://example.com/t.jpg")
        .duration(5)
        .build();

    SlideshowRepository.AssociatedSlideshowProjection projection1 = mock(SlideshowRepository.AssociatedSlideshowProjection.class);
    when(projection1.getImageId()).thenReturn(2L);
    when(projection1.getSlideshowId()).thenReturn(101L);

    SlideshowRepository.AssociatedSlideshowProjection projection2 = mock(SlideshowRepository.AssociatedSlideshowProjection.class);
    when(projection2.getImageId()).thenReturn(2L);
    when(projection2.getSlideshowId()).thenReturn(102L);

    when(imageRepository.findAll(any(Specification.class)))
        .thenReturn(Collections.singletonList(image));
    when(slideshowRepository.findAssociatedSlideshowsByImageIds(List.of(2L)))
        .thenReturn(List.of(projection1, projection2));

    // When
    ImageSearchResponse response = imageService.searchImagesWithResults(keyword);

    // Then
    assertNotNull(response);
    assertEquals(1, response.results().size());

    ImageResult result = response.results().get(0);
    assertEquals(2L, result.imageId());
    assertEquals("https://example.com/t.jpg", result.url());
    assertEquals(5, result.duration());
    assertEquals(List.of(101L, 102L), result.associatedSlideshows());
  }

  @Test
  void searchImagesWithResults_ShouldReturnEmptyIfNoResults() {
    // Given
    String keyword = "noresult";
    when(imageRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

    // When
    ImageSearchResponse response = imageService.searchImagesWithResults(keyword);

    // Then
    assertNotNull(response);
    assertTrue(response.results().isEmpty());
  }
}
