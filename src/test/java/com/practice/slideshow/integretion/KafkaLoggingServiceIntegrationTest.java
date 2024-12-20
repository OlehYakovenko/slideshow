package com.practice.slideshow.integretion;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.slideshow.SlideshowApplication;
import com.practice.slideshow.dto.LogEvent;
import com.practice.slideshow.dto.LogEventType;
import com.practice.slideshow.service.KafkaLoggingService;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(classes = SlideshowApplication.class)
@EmbeddedKafka(partitions = 1, topics = {"test-topic"}, brokerProperties = {
    "listeners=PLAINTEXT://localhost:9092", "port=9092"})
@DirtiesContext
class KafkaLoggingServiceIntegrationTest {

  private static final String TOPIC = "test-topic";

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private KafkaLoggingService kafkaLoggingService;

  @Autowired
  private EmbeddedKafkaBroker embeddedKafka;

  private final BlockingQueue<ConsumerRecord<Long, String>> records = new LinkedBlockingQueue<>();

  @KafkaListener(topics = TOPIC, groupId = "test-group")
  public void listen(ConsumerRecord<Long, String> record) {
    records.add(record);
  }

  @BeforeEach
  void setUp() {
    records.clear();
  }

  @AfterEach
  void tearDown() {
    records.clear();
  }

  @Test
  void testKafkaLoggingService() throws JsonProcessingException {
    // Create a LogEvent object
    LogEvent logEvent = new LogEvent(1L, LogEventType.ADD_IMAGE);

    // Send the message using KafkaLoggingService
    kafkaLoggingService.logAction(logEvent);

    // Set up a consumer to verify the message
    Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("test-group", "true",
        embeddedKafka);
    consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
    consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

    DefaultKafkaConsumerFactory<Long, String> consumerFactory = new DefaultKafkaConsumerFactory<>(
        consumerProps);
    Consumer<Long, String> consumer = consumerFactory.createConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(consumer, "test-topic");

    // Poll the consumer and verify the message
    ConsumerRecord<Long, String> receivedRecord = KafkaTestUtils.getSingleRecord(consumer,
        "test-topic");
    assertEquals(1L, receivedRecord.key());
    assertEquals(objectMapper.writeValueAsString(logEvent), receivedRecord.value());
  }
}