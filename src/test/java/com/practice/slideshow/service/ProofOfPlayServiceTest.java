package com.practice.slideshow.service;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.practice.slideshow.repository.ProofOfPlayRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class ProofOfPlayServiceTest {

  @Mock
  private ProofOfPlayRepository proofOfPlayRepository;

  @InjectMocks
  private ProofOfPlayService proofOfPlayService;

  @Test
  void shouldRecordProofOfPlaySuccessfully() {
    // Given
    Long slideshowId = 1L;
    Long imageId = 2L;

    // When
    proofOfPlayService.record(slideshowId, imageId);

    // Then
    verify(proofOfPlayRepository, times(1)).save(argThat(proof ->
        proof.getSlideshowId().equals(slideshowId) &&
            proof.getImageId().equals(imageId)
    ));
  }

  @Test
  void shouldNotSaveWhenSlideshowIdIsNull() {
    // Given
    Long slideshowId = null;
    Long imageId = 2L;

    // When
    proofOfPlayService.record(slideshowId, imageId);

    // Then
    verify(proofOfPlayRepository, never()).save(any());
  }

  @Test
  void shouldNotSaveWhenImageIdIsNull() {
    // Given
    Long slideshowId = 1L;

    // When
    proofOfPlayService.record(slideshowId, null);

    // Then
    verify(proofOfPlayRepository, never()).save(any());
  }

  @Test
  void shouldNotSaveWhenBothIdsAreNull() {

    // When
    proofOfPlayService.record(null, null);

    // Then
    verify(proofOfPlayRepository, never()).save(any());
  }
}