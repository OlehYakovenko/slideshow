package com.practice.slideshow.repository;

import com.practice.slideshow.entity.SlideshowEntity;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(excludeAutoConfiguration = {LiquibaseAutoConfiguration.class})
@Testcontainers
class SlideshowRepositoryTest {

  @Container
  private static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
      new PostgreSQLContainer<>("postgres:latest");

  @Autowired
  private SlideshowRepository slideshowRepository;

  @BeforeAll
  static void startContainer() {
    POSTGRES_CONTAINER.start();
    System.setProperty("spring.datasource.url", POSTGRES_CONTAINER.getJdbcUrl());
    System.setProperty("spring.datasource.username", POSTGRES_CONTAINER.getUsername());
    System.setProperty("spring.datasource.password", POSTGRES_CONTAINER.getPassword());
  }

  @AfterAll
  static void stopContainer() {
    POSTGRES_CONTAINER.stop();
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
