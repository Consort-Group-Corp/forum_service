package uz.consortgroup.forum_service.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbstractProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private TestProducer producer;

    @Test
    void sendEventToTopic_SuccessfulSend() {
        String topic = "test-topic";
        Object message = "test-message";
        when(kafkaTemplate.send(eq(topic), any())).thenReturn(CompletableFuture.completedFuture(mock(SendResult.class)));

        producer.sendEventToTopic(topic, List.of(message));

        verify(kafkaTemplate, times(1)).send(topic, message);
    }

    @Test
    void sendEventToTopic_MultipleMessages_SuccessfulSend() {
        String topic = "test-topic";
        List<Object> messages = List.of("msg1", "msg2", "msg3");
        when(kafkaTemplate.send(eq(topic), any())).thenReturn(CompletableFuture.completedFuture(mock(SendResult.class)));

        producer.sendEventToTopic(topic, messages);

        verify(kafkaTemplate, times(3)).send(anyString(), any());
    }

    @Test
    void sendEventToTopic_EmptyList_NoSendAttempted() {
        String topic = "test-topic";

        producer.sendEventToTopic(topic, List.of());

        verify(kafkaTemplate, never()).send(anyString(), any());
    }

    @Test
    void sendEventToTopic_KafkaError_ThrowsException() {
        String topic = "test-topic";
        Object message = "test-message";
        when(kafkaTemplate.send(eq(topic), any())).thenThrow(new RuntimeException("Kafka error"));

        assertThrows(RuntimeException.class, () -> 
            producer.sendEventToTopic(topic, List.of(message))
        );

        verify(kafkaTemplate, times(1)).send(topic, message);
    }

    @Test
    void sendEventToTopic_NullMessage_ThrowsException() {
        String topic = "test-topic";

        assertThrows(NullPointerException.class, () ->
            producer.sendEventToTopic(topic, List.of(null))
        );
    }

    private static class TestProducer extends AbstractProducer {
        public TestProducer(KafkaTemplate<String, Object> kafkaTemplate) {
            super(kafkaTemplate);
        }

        @Override
        protected String getTopic() {
            return "test-topic";
        }
    }
}