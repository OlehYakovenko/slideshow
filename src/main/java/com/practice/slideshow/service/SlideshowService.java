package com.practice.slideshow.service;

import com.practice.slideshow.entity.SlideshowEntity;
import com.practice.slideshow.entity.SlideshowImageId;
import com.practice.slideshow.entity.SlideshowImageLink;
import com.practice.slideshow.exception.ResourceNotFoundException;
import com.practice.slideshow.repository.SlideshowRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SlideshowService {

  private final SlideshowRepository slideshowRepository;
  private final ProofOfPlayService proofOfPlayService;
  private final ImageService imageService;

  @Transactional
  public SlideshowEntity addSlideshow(List<SlideshowImageData> imageDataList) {
    SlideshowEntity slideshow = SlideshowEntity.builder().build();
    slideshowRepository.save(slideshow);

    var links = imageDataList.stream().map(data -> {
      var image = imageService.addImage(data.url(), data.duration());
      return SlideshowImageLink.builder()
          .id(new SlideshowImageId(slideshow.getId(), image.getId()))
          .slideshow(slideshow)
          .image(image)
          .position(data.position())
          .build();
    }).toList();

    slideshow.getSlideshowImages().addAll(links);
    return slideshowRepository.save(slideshow);
  }

  @Transactional
  public void deleteSlideshow(Long id) {
    if (!slideshowRepository.existsById(id)) {
      throw new ResourceNotFoundException("Slideshow not found: id = " + id);
    }
    slideshowRepository.deleteById(id);
  }

  @Transactional(readOnly = true)
  public SlideshowEntity findById(Long id) {
    return slideshowRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Slideshow not found"));
  }

  @Transactional
  public void recordProofOfPlay(Long slideshowId, Long currentImageId) {
    proofOfPlayService.record(slideshowId, currentImageId);
  }

  public record SlideshowImageData(String url, int duration, int position) {

  }
}
