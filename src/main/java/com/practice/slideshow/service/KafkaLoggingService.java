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

  private final KafkaTemplate<Long, LogEvent> kafkaTemplate;

  @Value("${app.kafka.topic}")
  private String topic;

  /**
   * Logs a LogEvent object to the Kafka topic using ProducerRecord.
   *
   * @param logEvent The LogEvent to be logged.
   */
  public void logAction(LogEvent logEvent) {
    try {

      Objects.requireNonNull(logEvent, "Entity cannot be null");

      ProducerRecord<Long, LogEvent> producerRecord = buildProducerRecord(logEvent);
      log.info("Sending ProducerRecord: {}", producerRecord);

      CompletableFuture<SendResult<Long, LogEvent>> future = kafkaTemplate.send(producerRecord);
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
    } catch (JsonProcessingException e) {
      log.error("Failed to create ProducerRecord for event: {}", logEvent, e);
    } catch (Exception ex) {
      log.error("Failed to connect to Kafka: {}", ex.getMessage(), ex);
    }
  }

  /**
   * Builds a ProducerRecord for the given LogEvent.
   *
   * @param logEvent The LogEvent to be logged.
   * @return A ProducerRecord containing the LogEvent.
   * @throws JsonProcessingException If there is an error processing the JSON.
   */
  private ProducerRecord<Long, LogEvent> buildProducerRecord(LogEvent logEvent)
      throws JsonProcessingException {
    return new ProducerRecord<>(
        topic,
        null,
        null,
        logEvent.id(),
        logEvent,
        buildHeaders()
    );
  }

  /**
   * Builds the headers for the Kafka message.
   *
   * @return RecordHeaders containing the headers for the Kafka message.
   */
  private RecordHeaders buildHeaders() {
    RecordHeaders headers = new RecordHeaders();
    headers.add(new RecordHeader("mediaType", UUID.randomUUID().toString().getBytes()));
    return headers;
  }
}