package com.practice.slideshow.service;

import com.practice.slideshow.exception.InvalidRequestException;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Service to validate that a given URL points to a valid image resource.
 */
@Service
public class UrlValidationService {

  private static final List<String> ALLOWED_IMAGE_TYPES = List.of("image/jpeg", "image/png",
      "image/webp", "image/gif");

  private final WebClient webClient;

  public UrlValidationService(WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.build();
  }

  /**
   * Validates the provided URL to ensure it points to an image with an allowed content type.
   *
   * @param url The URL to validate.
   * @throws InvalidRequestException if the URL does not point to a valid image.
   */
  public void validateImageUrl(String url) {
    try {
      URI uri = URI.create(url);

      String contentType = webClient
          .get()
          .uri(uri)
          .retrieve()
          .onStatus(HttpStatusCode::isError, response ->
              response.createException()
                  .map(ex -> new InvalidRequestException("Failed to access URL: " + url)))
          .toBodilessEntity()
          .mapNotNull(response -> response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE))
          .block();

      if (contentType == null || ALLOWED_IMAGE_TYPES.stream().noneMatch(contentType::contains)) {
        throw new InvalidRequestException("The URL does not point to a valid image resource.");
      }

    } catch (Exception ex) {
      throw new InvalidRequestException("Error while validating URL: " + ex.getMessage());
    }
  }
}