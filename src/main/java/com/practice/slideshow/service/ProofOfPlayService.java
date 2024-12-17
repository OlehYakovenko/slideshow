package com.practice.slideshow.service;

import com.practice.slideshow.entity.ProofOfPlayEntity;
import com.practice.slideshow.repository.ProofOfPlayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProofOfPlayService {

  private final ProofOfPlayRepository proofOfPlayRepository;

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
