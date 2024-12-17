package com.practice.slideshow.specification;

import com.practice.slideshow.entity.ImageEntity;
import org.springframework.data.jpa.domain.Specification;

public class ImageSpecification {

  public static Specification<ImageEntity> hasUrlContaining(String keyword) {
    return (root, query, criteriaBuilder) ->
        keyword == null ? criteriaBuilder.conjunction()
            : criteriaBuilder.like(root.get("url"), "%" + keyword + "%");
  }
}
