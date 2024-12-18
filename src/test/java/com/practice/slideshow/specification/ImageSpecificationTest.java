package com.practice.slideshow.specification;

import static org.assertj.core.api.Assertions.assertThat;

import com.practice.slideshow.entity.ImageEntity;
import com.practice.slideshow.repository.ImageRepository;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class ImageSpecificationTest {

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