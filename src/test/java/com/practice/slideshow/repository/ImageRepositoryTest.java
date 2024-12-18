package com.practice.slideshow.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.practice.slideshow.entity.ImageEntity;
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
class ImageRepositoryTest {

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

  @Autowired
  private ImageRepository imageRepository;

  @AfterAll
  static void stopContainer() {
    postgres.stop();
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
