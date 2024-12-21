package com.practice.slideshow.repository;

import com.practice.slideshow.entity.SlideshowEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository interface for managing slideshow data.
 * Provides basic CRUD operations.
 */
public interface SlideshowRepository extends JpaRepository<SlideshowEntity, Long> {

  @Query("SELECT s FROM SlideshowEntity s JOIN s.slideshowImages li WHERE li.image.id = :imageId")
  List<SlideshowEntity> findByImageId(@Param("imageId") Long imageId);

  @Query("SELECT s.id AS slideshowId, sil.image.id AS imageId " +
      "FROM SlideshowEntity s " +
      "JOIN s.slideshowImages sil " +
      "WHERE sil.image.id IN :imageIds")
  List<AssociatedSlideshowProjection> findAssociatedSlideshowsByImageIds(@Param("imageIds") List<Long> imageIds);

  interface AssociatedSlideshowProjection {
    Long getImageId();
    Long getSlideshowId();
  }
}