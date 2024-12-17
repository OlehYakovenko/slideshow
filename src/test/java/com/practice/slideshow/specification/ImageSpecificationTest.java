package com.practice.slideshow.specification;

import com.practice.slideshow.entity.ImageEntity;
import com.practice.slideshow.repository.ImageRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
class ImageSpecificationTest {

  @Container
  private static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
      new PostgreSQLContainer<>("postgres:latest");

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
  void shouldFindImagesByUrlContainingKeyword() {
    // Given
    ImageEntity image1 = ImageEntity.builder().url("https://example.com/image1.jpg").duration(5)
        .build();
    ImageEntity image2 = ImageEntity.builder().url("https://example.com/other.jpg").duration(10)
        .build();

    imageRepository.saveAll(List.of(image1, image2));

    // When
    Specification<ImageEntity> spec = ImageSpecification.hasUrlContaining("image");
    List<ImageEntity> filteredImages = imageRepository.findAll(spec);

    // Then
    assertThat(filteredImages).hasSize(1);
    assertThat(filteredImages.get(0).getUrl()).isEqualTo("https://example.com/image1.jpg");
  }
}
