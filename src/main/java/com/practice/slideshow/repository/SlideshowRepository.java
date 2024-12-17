package com.practice.slideshow.repository;

import com.practice.slideshow.entity.SlideshowEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlideshowRepository extends JpaRepository<SlideshowEntity, Long> {

}