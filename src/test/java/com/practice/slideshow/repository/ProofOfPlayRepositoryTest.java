package com.practice.slideshow.repository;

import com.practice.slideshow.entity.ProofOfPlayEntity;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
class ProofOfPlayRepositoryTest {

  @Container
  private static final PostgreSQLContainer<?> POSTGRES_CONTAINER = new PostgreSQLContainer<>(
      "postgres:latest");

  @Autowired
  private ProofOfPlayRepository proofOfPlayRepository;

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
