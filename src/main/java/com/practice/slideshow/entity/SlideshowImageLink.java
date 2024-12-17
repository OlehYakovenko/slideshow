package com.practice.slideshow.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "slideshow_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SlideshowImageLink {

  @EmbeddedId
  private SlideshowImageId id;

  @ManyToOne
  @MapsId("slideshowId")
  @JoinColumn(name = "slideshow_id")
  private SlideshowEntity slideshow;

  @ManyToOne
  @MapsId("imageId")
  @JoinColumn(name = "image_id")
  private ImageEntity image;

  private int position;
}
