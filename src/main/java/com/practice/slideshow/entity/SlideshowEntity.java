package com.practice.slideshow.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "slideshows")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SlideshowEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @OneToMany(mappedBy = "slideshow", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("position ASC")
  private List<SlideshowImageLink> slideshowImages = new ArrayList<>();
}
