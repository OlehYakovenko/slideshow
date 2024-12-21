package com.practice.slideshow.specification;

import com.practice.slideshow.entity.ImageEntity;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specification class for filtering image entities.
 */
public class ImageSpecification {

  /**
   * Creates a specification to filter images containing the given keyword in their URL.
   *
   * @param keyword The keyword to search for.
   * @return The specification for filtering.
   */
  public static Specification<ImageEntity> hasUrlContaining(String keyword) {
    return (root, query, criteriaBuilder) ->
        keyword == null ? criteriaBuilder.conjunction()
            : criteriaBuilder.like(root.get("url"), "%" + keyword + "%");
  }
}
