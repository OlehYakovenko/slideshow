package com.practice.slideshow.repository;

import com.practice.slideshow.entity.ProofOfPlayEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing proof-of-play data.
 * Provides basic CRUD operations.
 */
public interface ProofOfPlayRepository extends JpaRepository<ProofOfPlayEntity, Long> {

}
