package com.practice.slideshow.service;

import com.practice.slideshow.entity.ProofOfPlayEntity;
import com.practice.slideshow.repository.ProofOfPlayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service responsible for recording proof of play events.
 *
 * <p>This service provides functionality to record associations between
 * a slideshow and an image, indicating that the image was displayed
 * during the slideshow. The data is persisted in the underlying repository.</p>
 *
 * <p>Dependencies are injected using constructor-based dependency injection.</p>
 */
@Service
@RequiredArgsConstructor
public class ProofOfPlayService {

  private final ProofOfPlayRepository proofOfPlayRepository;

  /**
   * Records a proof of play event linking a slideshow to an image.
   *
   * @param slideshowId the unique identifier of the slideshow.
   * @param imageId the unique identifier of the image.
   */
  public void record(Long slideshowId, Long imageId) {
    if (slideshowId == null || imageId == null) {
      return;
    }

    ProofOfPlayEntity proofOfPlay = ProofOfPlayEntity.builder()
        .slideshowId(slideshowId)
        .imageId(imageId)
        .build();

    proofOfPlayRepository.save(proofOfPlay);
  }
}
