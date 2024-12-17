//package com.practice.slideshow.service;
//import com.practice.slideshow.exception.InvalidRequestException;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
//import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
//import reactor.core.publisher.Mono;
//
//
//import static org.mockito.Mockito.*;
//import static org.junit.jupiter.api.Assertions.*;
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
//  private ResponseSpec responseSpec;
//
//  @InjectMocks
//  private UrlValidationService urlValidationService;
//
//  @Test
//  void shouldValidateCorrectImageUrl() {
//    // Given
//    String validUrl = "https://example.com/image.jpg";
//    HttpHeaders headers = new HttpHeaders();
//    headers.add(HttpHeaders.CONTENT_TYPE, "image/jpeg");
//
//    // Mock the WebClient behavior chain
//    when(webClientBuilder.build()).thenReturn(webClient);
//    when(webClient.head()).thenReturn(requestHeadersUriSpec);
//    when(requestHeadersUriSpec.uri(validUrl)).thenReturn(requestHeadersUriSpec);
//    when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
//    when(responseSpec.toBodilessEntity())
//        .thenReturn(Mono.just(new ResponseEntity<>(headers, HttpStatus.OK)));
//
//    // When
//    assertDoesNotThrow(() -> urlValidationService.validateImageUrl(validUrl));
//
//    // Then
//    verify(webClientBuilder, times(1)).build();
//    verify(webClient, times(1)).head();
//    verify(requestHeadersUriSpec, times(1)).uri(validUrl);
//    verify(responseSpec, times(1)).toBodilessEntity();
//  }
//
//  @Test
//  void shouldThrowInvalidRequestExceptionForInvalidContentType() {
//    // Given
//    String invalidUrl = "https://example.com/not-an-image.txt";
//    HttpHeaders headers = new HttpHeaders();
//    headers.add(HttpHeaders.CONTENT_TYPE, "text/plain");
//
//    // Mock the WebClient behavior chain
//    when(webClientBuilder.build()).thenReturn(webClient);
//    when(webClient.head()).thenReturn(requestHeadersUriSpec);
//    when(requestHeadersUriSpec.uri(invalidUrl)).thenReturn(requestHeadersUriSpec);
//    when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
//    when(responseSpec.toBodilessEntity())
//        .thenReturn(Mono.just(new ResponseEntity<>(headers, HttpStatus.OK)));
//
//    // When & Then
//    assertThrows(InvalidRequestException.class, () -> urlValidationService.validateImageUrl(invalidUrl));
//  }
//
//  @Test
//  void shouldThrowInvalidRequestExceptionWhenUrlUnreachable() {
//    // Given
//    String unreachableUrl = "https://invalid-url.com";
//
//    // Mock the WebClient behavior chain
//    when(webClientBuilder.build()).thenReturn(webClient);
//    when(webClient.head()).thenReturn(requestHeadersUriSpec);
//    when(requestHeadersUriSpec.uri(unreachableUrl)).thenReturn(requestHeadersUriSpec);
//    when(requestHeadersUriSpec.retrieve()).thenThrow(new RuntimeException("Connection failed"));
//
//    // When & Then
//    assertThrows(InvalidRequestException.class, () -> urlValidationService.validateImageUrl(unreachableUrl));
//  }
//}