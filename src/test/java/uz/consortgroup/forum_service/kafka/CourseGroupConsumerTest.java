package uz.consortgroup.forum_service.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import uz.consortgroup.forum_service.event.course_group.CourseGroupOpenedEvent;
import uz.consortgroup.forum_service.service.processor.CourseGroupOpenedEventProcessor;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CourseGroupConsumerTest {

    @Mock
    private CourseGroupOpenedEventProcessor processor;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private CourseGroupConsumer consumer;

    @Test
    void onMessage_SuccessfulProcessing() {
        CourseGroupOpenedEvent event = new CourseGroupOpenedEvent();
        List<CourseGroupOpenedEvent> events = List.of(event);

        consumer.onMessage(events, acknowledgment);

        verify(processor).process(List.of(event));
        verify(acknowledgment).acknowledge();
    }

    @Test
    void onMessage_EmptyList() {
        consumer.onMessage(List.of(), acknowledgment);

        verify(processor, never()).process(any());
        verify(acknowledgment).acknowledge();
    }

    @Test
    void onMessage_NullList() {
        assertThrows(NullPointerException.class, 
            () -> consumer.onMessage(null, acknowledgment));
    }

    @Test
    void onMessage_ProcessingError() {
        CourseGroupOpenedEvent event = new CourseGroupOpenedEvent();
        List<CourseGroupOpenedEvent> events = List.of(event);

        doThrow(new RuntimeException("Processing error"))
                .when(processor).process(any());

        assertDoesNotThrow(() -> consumer.onMessage(events, acknowledgment));

        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void handleMessage_Success() {
        CourseGroupOpenedEvent event = new CourseGroupOpenedEvent();

        consumer.handleMessage(event);

        verify(processor).process(List.of(event));
    }

    @Test
    void getMessageId_ReturnsCorrectId() {
        UUID expectedId = UUID.randomUUID();
        CourseGroupOpenedEvent event = new CourseGroupOpenedEvent();
        event.setMessageId(expectedId);

        UUID actualId = consumer.getMessageId(event);

        assertEquals(expectedId, actualId);
    }
}