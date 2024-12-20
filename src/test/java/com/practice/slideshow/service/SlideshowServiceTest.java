package com.practice.slideshow.service;

import com.practice.slideshow.dto.LogEvent;
import com.practice.slideshow.dto.LogEventType;
import com.practice.slideshow.exception.ResourceNotFoundException;
import com.practice.slideshow.repository.SlideshowRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SlideshowServiceTest {

  @Mock
  private SlideshowRepository slideshowRepository;

  @Mock
  private KafkaLoggingService kafkaLoggingService;

  @InjectMocks
  private SlideshowService slideshowService;

  @Test
  void shouldDeleteSlideshowSuccessfully() {
    // Given
    Long slideshowId = 1L;
    given(slideshowRepository.existsById(slideshowId)).willReturn(true);
    doNothing().when(kafkaLoggingService).logAction(new LogEvent(1L, LogEventType.DELETE_SLIDESHOW));

    // When
    slideshowService.deleteSlideshow(slideshowId);

    // Then
    verify(slideshowRepository).deleteById(slideshowId);
    verify(kafkaLoggingService).logAction(new LogEvent(1L, LogEventType.DELETE_SLIDESHOW));
  }

  @Test
  void shouldThrowWhenSlideshowNotFoundOnDelete() {
    // Given
    Long slideshowId = 1L;
    given(slideshowRepository.existsById(slideshowId)).willReturn(false);

    // When & Then
    assertThrows(ResourceNotFoundException.class,
        () -> slideshowService.deleteSlideshow(slideshowId));
  }
}
