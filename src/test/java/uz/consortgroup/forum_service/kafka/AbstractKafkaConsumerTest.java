package uz.consortgroup.forum_service.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AbstractKafkaConsumerTest {

    @Mock
    private Acknowledgment acknowledgment;

    private static class TestMessage {
        private final UUID id;
        private final String content;

        public TestMessage(UUID id, String content) {
            this.id = id;
            this.content = content;
        }

        public UUID getId() {
            return id;
        }
    }

    private static class TestConsumer extends AbstractKafkaConsumer<TestMessage> {
        @Override
        protected void handleMessage(TestMessage message) {
            if ("error".equals(message.content)) {
                throw new RuntimeException("Test error");
            }
        }

        @Override
        protected UUID getMessageId(TestMessage message) {
            return message.getId();
        }
    }

    @Test
    void processBatch_SuccessfulProcessing_Acknowledges() {
        TestConsumer consumer = new TestConsumer();
        List<TestMessage> messages = List.of(
            new TestMessage(UUID.randomUUID(), "msg1"),
            new TestMessage(UUID.randomUUID(), "msg2")
        );

        consumer.processBatch(messages, acknowledgment);

        verify(acknowledgment).acknowledge();
    }

    @Test
    void processBatch_WithNullMessages_FiltersNulls() {
        TestConsumer consumer = new TestConsumer();
        List<TestMessage> messages = new ArrayList<>();
        messages.add(new TestMessage(UUID.randomUUID(), "msg1"));
        messages.add(null);
        messages.add(new TestMessage(UUID.randomUUID(), "msg2"));

        consumer.processBatch(messages, acknowledgment);

        verify(acknowledgment).acknowledge();
    }

    @Test
    void processBatch_MessageProcessingFails_StillAcknowledges() {
        TestConsumer consumer = new TestConsumer();
        List<TestMessage> messages = List.of(
            new TestMessage(UUID.randomUUID(), "error"),
            new TestMessage(UUID.randomUUID(), "msg2")
        );

        consumer.processBatch(messages, acknowledgment);

        verify(acknowledgment).acknowledge();
    }

    @Test
    void processBatch_InterruptedException_LogsError() {
        TestConsumer consumer = spy(new TestConsumer());
        List<TestMessage> messages = List.of(new TestMessage(UUID.randomUUID(), "msg1"));

        doThrow(new RuntimeException(new InterruptedException("Test interrupt")))
                .when(consumer).processBatch(any(), any());

        assertThrows(RuntimeException.class, () -> {
            consumer.processBatch(messages, acknowledgment);
        });

        verify(acknowledgment, never()).acknowledge();
    }

    @Test
    void processBatch_ExecutionException_LogsError() {
        TestConsumer consumer = spy(new TestConsumer());
        List<TestMessage> messages = List.of(new TestMessage(UUID.randomUUID(), "msg1"));

        doAnswer(invocation -> {
            throw new ExecutionException(new RuntimeException("Test error"));
        }).when(consumer).processBatch(any(), any());

        assertThrows(ExecutionException.class, () -> {
            consumer.processBatch(messages, acknowledgment);
        });

        verify(acknowledgment, never()).acknowledge();
    }
}