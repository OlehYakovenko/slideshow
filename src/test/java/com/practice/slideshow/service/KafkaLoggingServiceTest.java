package com.practice.slideshow.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class KafkaLoggingServiceTest {

  @Mock
  private KafkaTemplate<String, String> kafkaTemplate;

  @InjectMocks
  private KafkaLoggingService kafkaLoggingService;

  @Test
  void shouldLogActionSuccessfully() {
    // Given
    String actionType = "ADD_IMAGE";
    String message = "Image added with ID: 1";
    String expectedLog = "Action: ADD_IMAGE | Details: Image added with ID: 1";

    // When
    kafkaLoggingService.logAction(actionType, message);

    // Then
    verify(kafkaTemplate, times(1)).send("api-actions", expectedLog);
  }

  @Test
  void shouldNotLogWhenActionTypeOrMessageIsNull() {
    // When
    kafkaLoggingService.logAction(null, "Some message");
    kafkaLoggingService.logAction("ADD_IMAGE", null);

    // Then
    verify(kafkaTemplate, never()).send(anyString(), anyString());
  }

  @Test
  void shouldHandleKafkaSendFailureGracefully() {
    // Given
    String actionType = "DELETE_IMAGE";
    String message = "Image deleted with ID: 1";
    String expectedLog = "Action: DELETE_IMAGE | Details: Image deleted with ID: 1";

    doThrow(new RuntimeException("Kafka unavailable"))
        .when(kafkaTemplate).send("api-actions", expectedLog);

    // When
    assertDoesNotThrow(() -> kafkaLoggingService.logAction(actionType, message));

    // Then
    verify(kafkaTemplate, times(1)).send("api-actions", expectedLog);
  }
}
