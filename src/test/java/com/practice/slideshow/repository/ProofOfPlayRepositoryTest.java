package com.practice.slideshow.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.practice.slideshow.entity.ProofOfPlayEntity;
import java.util.List;
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
class ProofOfPlayRepositoryTest {

  @Autowired
  private ProofOfPlayRepository proofOfPlayRepository;

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
  void shouldSaveAndRetrieveProofOfPlay() {
    // Given
    ProofOfPlayEntity proofOfPlay = ProofOfPlayEntity.builder()
        .slideshowId(1L)
        .imageId(2L)
        .build();

    // When
    ProofOfPlayEntity savedEntity = proofOfPlayRepository.save(proofOfPlay);

    // Then
    assertThat(savedEntity.getId()).isNotNull();
    assertThat(savedEntity.getSlideshowId()).isEqualTo(1L);
    assertThat(savedEntity.getImageId()).isEqualTo(2L);
  }

  @Test
  void shouldFindAllProofOfPlayEntities() {
    // Given
    ProofOfPlayEntity proof1 = ProofOfPlayEntity.builder().slideshowId(1L).imageId(2L).build();
    ProofOfPlayEntity proof2 = ProofOfPlayEntity.builder().slideshowId(1L).imageId(3L).build();

    proofOfPlayRepository.saveAll(List.of(proof1, proof2));

    // When
    List<ProofOfPlayEntity> allEntities = proofOfPlayRepository.findAll();

    // Then
    assertThat(allEntities).hasSize(2);
  }
}
