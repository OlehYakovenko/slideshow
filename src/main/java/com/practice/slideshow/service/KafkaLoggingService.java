package com.practice.slideshow.service;

import com.practice.slideshow.dto.ImageResponse;
import com.practice.slideshow.entity.ImageEntity;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

/**
 * Service responsible for logging significant API actions to a Kafka topic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaLoggingService {

  private final KafkaTemplate<String, ImageResponse> kafkaTemplate;

  @Value("${app.kafka.topic}")
  private String topic;

  /**
   * Logs an ImageResponse object to the Kafka topic using ProducerRecord.
   *
   * @param actionType Action type (e.g., "ADD_IMAGE", "DELETE_IMAGE", etc.)
   * @param entity     The ImageEntity to be converted into ImageResponse.
   */
  public void logAction(String actionType, ImageEntity entity) {
    if (actionType == null || entity == null) {
      log.warn("Action type or entity is null. Skipping log action.");
      return;
    }

    String mediaType = actionType.contains("IMAGE") ? "image" : "audio";
    ImageResponse response = ImageResponse.fromEntity(entity);
    RecordHeaders headers = new RecordHeaders();
    headers.add(new RecordHeader("mediaType", mediaType.getBytes()));
    ProducerRecord<String, ImageResponse> producerRecord = new ProducerRecord<>(
        topic,
        null,
        null,
        String.valueOf(response.imageId()),
        response,
        headers
    );

    log.info("Sending ImageResponse to Kafka. ActionType: {}, MediaType: {}, Topic: {}",
        actionType, mediaType, topic);

    CompletableFuture<SendResult<String, ImageResponse>> future = kafkaTemplate.send(producerRecord);
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