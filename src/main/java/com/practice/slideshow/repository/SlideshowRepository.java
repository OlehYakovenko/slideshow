package com.practice.slideshow.repository;

import com.practice.slideshow.entity.SlideshowEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing slideshow data.
 * Provides basic CRUD operations.
 */
public interface SlideshowRepository extends JpaRepository<SlideshowEntity, Long> {

}