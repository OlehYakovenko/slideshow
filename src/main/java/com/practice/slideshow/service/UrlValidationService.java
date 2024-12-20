package com.practice.slideshow.service;

import com.practice.slideshow.exception.InvalidRequestException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.List;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Service to validate that a given URL points to a valid image resource.
 */
@Service
@Slf4j
public class UrlValidationService {

  private static final List<String> ALLOWED_IMAGE_TYPES = List.of("image/jpeg", "image/png", "image/webp", "image/gif");
  private final WebClient webClient;

  public UrlValidationService(WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.build();
  }

  /**
   * Validates the provided URL to ensure it points to an image with an allowed content type and that the downloaded file is actually an image.
   *
   * @param url The URL to validate.
   * @throws InvalidRequestException if the URL does not point to a valid image.
   */
  public void validateImageUrl(String url) {
    log.info("Validating image URL: {}", url);

    try {
      URI uri = URI.create(url);
      String contentType = webClient
          .get()
          .uri(uri)
          .retrieve()
          .onStatus(HttpStatusCode::isError, response -> {
            log.error("Failed to validate URL: {} with status code: {}", url, response.statusCode());
            return response.createException()
                .map(ex -> new InvalidRequestException("Failed to access URL: " + url));
          })
          .toBodilessEntity()
          .mapNotNull(response -> response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE))
          .block();

      if (contentType == null || ALLOWED_IMAGE_TYPES.stream().noneMatch(contentType::contains)) {
        log.error("Invalid Content-Type for URL {}: {}", url, contentType);
        throw new InvalidRequestException("The URL does not point to a valid image resource by content-type.");
      }

      byte[] imageBytes = webClient
          .get()
          .uri(uri)
          .retrieve()
          .bodyToMono(byte[].class)
          .block();

      if (imageBytes == null || imageBytes.length == 0) {
        throw new InvalidRequestException("Downloaded file is empty or not accessible.");
      }

      try (ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes)) {
        BufferedImage image = ImageIO.read(bais);
        if (image == null) {
          log.error("File content check failed for URL: {} - not a valid image", url);
          throw new InvalidRequestException("The file does not match expected image format.");
        }
      }

      log.info("URL validation successful for: {}", url);

    } catch (Exception ex) {
      log.error("Error while validating URL: {}", url, ex);
      throw new InvalidRequestException("Error while validating URL: " + ex.getMessage());
    }
  }
}