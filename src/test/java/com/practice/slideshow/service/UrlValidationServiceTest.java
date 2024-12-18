//package com.practice.slideshow.service;
//
//import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//import com.practice.slideshow.exception.InvalidRequestException;
//import java.net.URI;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
//import reactor.core.publisher.Mono;
//
//@ExtendWith(MockitoExtension.class)
//class UrlValidationServiceTest {
//
//  @Mock
//  private WebClient.Builder webClientBuilder;
//
//  @Mock
//  private WebClient webClient;
//
//  @Mock
//  private RequestHeadersUriSpec<?> requestHeadersUriSpec;
//
//  @Mock
//  private WebClient.ResponseSpec responseSpec;
//
//  @InjectMocks
//  private UrlValidationService urlValidationService;
//
//  @BeforeEach
//  void setup() {
//    MockitoAnnotations.openMocks(this);
//    when(webClientBuilder.build()).thenReturn(webClient);
//    // Приведення типів до сирих типів при моканні
//    when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec)requestHeadersUriSpec);
//    when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersUriSpec);
//    when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
//  }
//
//  @Test
//  @SuppressWarnings("unchecked")
//  void testValidImageUrl() {
//    String validUrl = "https://example.com/image.jpg";
//    HttpHeaders headers = new HttpHeaders();
//    headers.add(HttpHeaders.CONTENT_TYPE, "image/jpeg");
//
//    when(webClient.get()).thenReturn(requestHeadersUriSpec);
//    when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersUriSpec);
//    when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
//    when(responseSpec.toBodilessEntity()).thenReturn(Mono.just(new org.springframework.http.ResponseEntity<>(headers, HttpStatus.OK)));
//
//    UrlValidationService service = new UrlValidationService(webClientBuilder);
//    assertDoesNotThrow(() -> service.validateImageUrl(validUrl));
//  }
//
//  @Test
//  void validateImageUrl_ShouldPassForValidImage() {
//    String validUrl = "https://example.com/image.jpg";
//    HttpHeaders headers = new HttpHeaders();
//    headers.add(HttpHeaders.CONTENT_TYPE, "image/jpeg");
//
//    when(webClient.get()).thenReturn(requestHeadersUriSpec);
//    when(requestHeadersUriSpec.uri(URI.create(validUrl))).thenReturn(requestHeadersUriSpec);
//    when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
//    when(responseSpec.toBodilessEntity()).thenReturn(Mono.just(new org.springframework.http.ResponseEntity<>(headers, HttpStatus.OK)));
//
//    assertDoesNotThrow(() -> urlValidationService.validateImageUrl(validUrl));
//  }
//
//  @Test
//  void validateImageUrl_ShouldThrowForInvalidContentType() {
//    String invalidUrl = "https://example.com/file.txt";
//    HttpHeaders headers = new HttpHeaders();
//    headers.add(HttpHeaders.CONTENT_TYPE, "text/plain");
//
//    when(webClient.get()).thenReturn(requestHeadersUriSpec);
//    when(requestHeadersUriSpec.uri(URI.create(invalidUrl))).thenReturn(requestHeadersUriSpec);
//    when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
//    when(responseSpec.toBodilessEntity()).thenReturn(Mono.just(new org.springframework.http.ResponseEntity<>(headers, HttpStatus.OK)));
//
//    InvalidRequestException ex = assertThrows(InvalidRequestException.class, () -> urlValidationService.validateImageUrl(invalidUrl));
//    assertTrue(ex.getMessage().contains("does not point to a valid image resource"));
//  }
//
//  @Test
//  void validateImageUrl_ShouldThrowIfUnreachable() {
//    String unreachableUrl = "https://invalid-url.com";
//
//    when(webClient.get()).thenReturn(requestHeadersUriSpec);
//    when(requestHeadersUriSpec.uri(URI.create(unreachableUrl))).thenReturn(requestHeadersUriSpec);
//
//    when(requestHeadersUriSpec.retrieve()).thenThrow(new RuntimeException("Connection failed"));
//
//    InvalidRequestException ex = assertThrows(InvalidRequestException.class, () -> urlValidationService.validateImageUrl(unreachableUrl));
//    assertTrue(ex.getMessage().contains("Error while validating URL"));
//  }
//}