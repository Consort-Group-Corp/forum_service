package uz.consortgroup.forum_service.service.processor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.consortgroup.forum_service.event.course_group.CourseGroupOpenedEvent;
import uz.consortgroup.forum_service.service.service.ForumService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CourseGroupOpenedEventProcessorTest {

    @Mock
    private ForumService forumService;

    @InjectMocks
    private CourseGroupOpenedEventProcessor processor;

    @Test
    void process_Success() {
        UUID messageId = UUID.randomUUID();
        CourseGroupOpenedEvent event = new CourseGroupOpenedEvent();
        event.setMessageId(messageId);
        List<CourseGroupOpenedEvent> events = List.of(event);

        processor.process(events);

        verify(forumService).createForum(events);
    }


    @Test
    void process_ServiceThrowsException() {
        List<CourseGroupOpenedEvent> events = List.of(new CourseGroupOpenedEvent());
        doThrow(new RuntimeException("Test error")).when(forumService).createForum(events);

        assertThrows(RuntimeException.class, () -> processor.process(events));
    }
}