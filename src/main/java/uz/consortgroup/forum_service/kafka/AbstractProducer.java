package uz.consortgroup.forum_service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Component
public abstract class AbstractProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    protected void sendEventToTopic(String topic, List<Object> messages) {
        messages.forEach(message -> {
            Objects.requireNonNull(message, "Message cannot be null");
            try {
                log.info("Sending message to Kafka topic '{}' : {}", topic, message);
                kafkaTemplate.send(topic, message);
            } catch (Exception ex) {
                log.error("Failed to send message to Kafka topic '{}'", topic, ex);
                throw ex;
            }
        });
    }

    protected abstract String getTopic();
}
