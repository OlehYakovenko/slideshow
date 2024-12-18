package com.practice.slideshow.service;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;

@ExtendWith(MockitoExtension.class)
class KafkaLoggingServiceTest {

  @Mock
  KafkaTemplate<String, String> kafkaTemplate;

  @InjectMocks
  KafkaLoggingService kafkaLoggingService;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void logAction_ShouldSendMessageIfNotNull() {
    String actionType = "ADD_IMAGE";
    String message = "Image added";

    ProducerRecord<String, String> record = new ProducerRecord<>("api-actions", "Action: ADD_IMAGE | Details: Image added");
    RecordMetadata metadata = new RecordMetadata(new TopicPartition("api-actions", 0), 0,0,0L, 0L, 0,0);
    SendResult<String, String> sendResult = new SendResult<>(record, metadata);
    CompletableFuture<SendResult<String,String>> future = CompletableFuture.completedFuture(sendResult);

    when(kafkaTemplate.send(any(Message.class))).thenReturn(future);

    kafkaLoggingService.logAction(actionType, message);

    verify(kafkaTemplate).send(any(Message.class));
  }

  @Test
  void logAction_ShouldNotSendIfActionTypeNull() {
    kafkaLoggingService.logAction(null, "Message");
    verify(kafkaTemplate, never()).send(anyString(), anyString());
  }

  @Test
  void logAction_ShouldNotSendIfMessageNull() {
    kafkaLoggingService.logAction("ADD_IMAGE", null);
    verify(kafkaTemplate, never()).send(anyString(), anyString());
  }
}
