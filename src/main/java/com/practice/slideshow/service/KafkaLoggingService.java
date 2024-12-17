package com.practice.slideshow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Logs significant API actions to a Kafka topic.
 */
@Service
@RequiredArgsConstructor
public class KafkaLoggingService {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private static final String TOPIC = "api-actions";

  /**
   * Logs a message to the Kafka topic.
   *
   * @param actionType The type of action (e.g., "ADD_IMAGE", "DELETE_IMAGE").
   * @param message    The log message.
   */
  public void logAction(String actionType, String message) {
    if (actionType == null || message == null) {
      return;
    }
    String logMessage = String.format("Action: %s | Details: %s", actionType, message);
    try {
      kafkaTemplate.send(TOPIC, logMessage);
    } catch (Exception e) {
      System.err.println("Kafka send failed: " + e.getMessage());
    }
  }
}