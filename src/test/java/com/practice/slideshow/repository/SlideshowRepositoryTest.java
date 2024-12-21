package com.practice.slideshow.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.practice.slideshow.entity.SlideshowEntity;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class SlideshowRepositoryTest {

  @Autowired
  private SlideshowRepository slideshowRepository;

  @Container
  private static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:latest");

  @DynamicPropertySource
  static void redisProperties(DynamicPropertyRegistry registry) {
    postgres.start();
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @AfterAll
  static void stopContainer() {
    postgres.stop();
  }

  @Test
  void shouldSaveAndRetrieveSlideshow() {
    // Given
    SlideshowEntity slideshow = SlideshowEntity.builder()
        .createdAt(LocalDateTime.now())
        .build();

    // When
    SlideshowEntity savedSlideshow = slideshowRepository.save(slideshow);
    Optional<SlideshowEntity> retrievedSlideshow = slideshowRepository.findById(
        savedSlideshow.getId());

    // Then
    assertThat(retrievedSlideshow).isPresent();
    assertThat(retrievedSlideshow.get().getId()).isEqualTo(savedSlideshow.getId());
    assertThat(retrievedSlideshow.get().getCreatedAt()).isNotNull();
  }

  @Test
  void shouldDeleteSlideshowById() {
    // Given
    SlideshowEntity slideshow = SlideshowEntity.builder()
        .createdAt(LocalDateTime.now())
        .build();

    SlideshowEntity savedSlideshow = slideshowRepository.save(slideshow);

    // When
    slideshowRepository.deleteById(savedSlideshow.getId());
    Optional<SlideshowEntity> deletedSlideshow = slideshowRepository.findById(
        savedSlideshow.getId());

    // Then
    assertThat(deletedSlideshow).isEmpty();
  }
}
