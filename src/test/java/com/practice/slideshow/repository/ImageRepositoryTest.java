package com.practice.slideshow.repository;

import com.practice.slideshow.entity.ImageEntity;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
class ImageRepositoryTest {

  @Container
  private static final PostgreSQLContainer<?> POSTGRES_CONTAINER = new PostgreSQLContainer<>(
      "postgres:latest");

  @Autowired
  private ImageRepository imageRepository;

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
  void shouldSaveAndFindImageById() {
    // Given
    ImageEntity image = ImageEntity.builder()
        .url("https://example.com/image.jpg")
        .duration(5)
        .build();

    // When
    ImageEntity savedImage = imageRepository.save(image);
    Optional<ImageEntity> retrievedImage = imageRepository.findById(savedImage.getId());

    // Then
    assertThat(retrievedImage).isPresent();
    assertThat(retrievedImage.get().getUrl()).isEqualTo("https://example.com/image.jpg");
    assertThat(retrievedImage.get().getDuration()).isEqualTo(5);
  }

  @Test
  void shouldDeleteImage() {
    // Given
    ImageEntity image = ImageEntity.builder()
        .url("https://example.com/image.jpg")
        .duration(5)
        .build();

    ImageEntity savedImage = imageRepository.save(image);

    // When
    imageRepository.deleteById(savedImage.getId());
    Optional<ImageEntity> retrievedImage = imageRepository.findById(savedImage.getId());

    // Then
    assertThat(retrievedImage).isEmpty();
  }
}
