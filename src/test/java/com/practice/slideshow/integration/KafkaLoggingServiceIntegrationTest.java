package com.practice.slideshow.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.practice.slideshow.dto.LogEvent;
import com.practice.slideshow.dto.LogEventType;
import com.practice.slideshow.service.KafkaLoggingService;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

@EmbeddedKafka(topics = {"api-actions"}, partitions = 3)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"})
class KafkaLoggingServiceIntegrationTest extends BaseIntegrationTest {

  @Autowired
  private KafkaLoggingService kafkaLoggingService;
  @Autowired
  private EmbeddedKafkaBroker embeddedKafkaBroker;

  private Consumer<Long, LogEvent> consumer;

  @BeforeEach
  void setUp() {
    Map<String, Object> configs = new HashMap<>(
        KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker));
    configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

    JsonDeserializer<LogEvent> jsonDeserializer = new JsonDeserializer<>();
    jsonDeserializer.addTrustedPackages("*");

    consumer = new DefaultKafkaConsumerFactory<>(configs, new LongDeserializer(), jsonDeserializer).createConsumer();
    embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
  }

  @AfterEach
  void tearDown() {
    consumer.close();
  }

  @Test
  @Timeout(5)
  void givenCorrectEvent_whenSendLogEvent_thenCorrectEventIsConsumed() {
    // Given logEvent
    LogEvent logEvent = new LogEvent(1L, LogEventType.ADD_IMAGE);

    // When send event to kafka
    kafkaLoggingService.logAction(logEvent);

    // Then event should be correct
    ConsumerRecords<Long, LogEvent> consumerRecords = KafkaTestUtils.getRecords(consumer);

    assert consumerRecords.count() == 1;
    consumerRecords.forEach(record-> assertThat(record.value()).isEqualTo(logEvent));
  }
}