package com.practice.slideshow.service;

import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * Service responsible for logging significant API actions to a Kafka topic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaLoggingService {

  private final KafkaTemplate<String, String> kafkaTemplate;

  @Value("${app.kafka.topic}")
  private String topic;

  /**
   * Logs a message to the Kafka topic.
   *
   * @param actionType The type of action (e.g., "ADD_IMAGE", "DELETE_IMAGE").
   * @param message    The log message.
   */
  public void logAction(String actionType, String message) {
    if (actionType == null || message == null) {
      log.warn("Action type or message is null. Skipping log action.");
      return;
    }
    String logMessage = String.format("Action: %s | Details: %s", actionType, message);
    String mediaType = actionType.contains("IMAGE") ? "image" : "audio";
    Message<String> msg = MessageBuilder.withPayload(logMessage)
        .setHeader(KafkaHeaders.TOPIC, topic)
        .setHeader("mediaType", mediaType)
        .build();

    log.info("Sending message to Kafka. ActionType: {}, MediaType: {}, Topic: {}", actionType,
        mediaType, topic);


    CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(msg);
    future.whenComplete((result, ex) -> {
      if (ex == null) {
        log.info("Message sent successfully to topic: {}, offset: {}",
            result.getRecordMetadata().topic(),
            result.getRecordMetadata().offset());
      } else {
        log.error("Failed to send message to Kafka: {}", ex.getMessage(), ex);
      }
    });
  }
}