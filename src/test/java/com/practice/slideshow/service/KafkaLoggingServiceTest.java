package com.practice.slideshow.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.slideshow.dto.LogEvent;
import com.practice.slideshow.dto.LogEventType;
import java.util.concurrent.CompletableFuture;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class KafkaLoggingServiceTest {

  @Mock
  KafkaTemplate<Long, String> kafkaTemplate;

  @InjectMocks
  KafkaLoggingService kafkaLoggingService;

  @Mock
  ObjectMapper objectMapper;

  @BeforeEach
  void setup() {
    ReflectionTestUtils.setField(kafkaLoggingService, "topic", "test-topic");
  }

  @Test
  void logAction_ShouldSendMessageIfNotNull() throws Exception {
    // Arrange
    LogEvent logEvent = LogEvent.builder()
        .id(1L)
        .eventType(LogEventType.ADD_IMAGE)
        .build();

    ProducerRecord<Long, String> producerRecord = new ProducerRecord<>("test-topic", 1L,
        "log-event");
    RecordMetadata metadata = new RecordMetadata(
        new TopicPartition("test-topic", 0), 0, 0, System.currentTimeMillis(), 0L, 0, 0);
    SendResult<Long, String> sendResult = new SendResult<>(producerRecord, metadata);
    CompletableFuture<SendResult<Long, String>> future = CompletableFuture.completedFuture(
        sendResult);

    when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);
    when(objectMapper.writeValueAsString(logEvent)).thenReturn("log-event");

    // Act
    kafkaLoggingService.logAction(logEvent);

    // Assert
    verify(kafkaTemplate).send(any(ProducerRecord.class));
  }

  @Test
  void logAction_ShouldNotSendIfLogEventIsNull() {
    // Act
    kafkaLoggingService.logAction(null);

    // Assert
    verify(kafkaTemplate, never()).send(any(ProducerRecord.class));
  }
}