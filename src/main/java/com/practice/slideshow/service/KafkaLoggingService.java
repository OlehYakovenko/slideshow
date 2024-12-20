package com.practice.slideshow.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.slideshow.dto.LogEvent;
import java.util.Objects;
import java.util.UUID;
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

  private final KafkaTemplate<Long, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  @Value("${app.kafka.topic}")
  private String topic;

  /**
   * Logs an ImageResponse object to the Kafka topic using ProducerRecord.
   *
   * @param logEvent     The ImageEntity to be converted into ImageResponse.
   */

  public void logAction(LogEvent logEvent) {
    try {
      Objects.requireNonNull(logEvent, "Entity cannot be null");

      ProducerRecord<Long, String> producerRecord = buildProducerRecord(
          logEvent);
      log.info("Sending ProducerRecord: {}", producerRecord);

      CompletableFuture<SendResult<Long, String>> future = kafkaTemplate.send(producerRecord);
      future.whenComplete((result, ex) -> {
        if (ex == null) {
          log.info("Message sent successfully to topic: {}, partition: {}, offset: {}",
              result.getRecordMetadata().topic(),
              result.getRecordMetadata().partition(),
              result.getRecordMetadata().offset());
        } else {
          log.error("Failed to send message to Kafka: {}", ex.getMessage(), ex);
        }
      });
    } catch (JsonProcessingException e){
      log.error("Failed to create ProducerRecord for event: {}", logEvent, e);
    } catch (Exception ex){
      log.error("Failed to connect to Kafka: {}", ex.getMessage(), ex);
    }
  }

  private ProducerRecord<Long, String> buildProducerRecord(LogEvent logEvent)
      throws JsonProcessingException {
    return new ProducerRecord<>(
        topic,
        null,
        null,
        logEvent.id(),
        objectMapper.writeValueAsString(logEvent),
        buildHeaders()
    );
  }

  private RecordHeaders buildHeaders() {
    RecordHeaders headers = new RecordHeaders();
    headers.add(new RecordHeader("mediaType", UUID.randomUUID().toString().getBytes()));
    return headers;
  }
}