package uz.consortgroup.forum_service.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import uz.consortgroup.forum_service.topic.KafkaTopic;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseForumGroupEventProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private KafkaTopic kafkaTopic;

    @InjectMocks
    private CourseForumGroupEventProducer producer;

    @Test
    void sendCourseForumGroupCreatedEvents_Success() {
        String topicName = "course-forum-group-topic";
        List<Object> messages = List.of("message1", "message2");
        
        when(kafkaTopic.getCourseForumGroup()).thenReturn(topicName);
        
        producer.sendCourseForumGroupCreatedEvents(messages);
        
        verify(kafkaTemplate, times(2)).send(eq(topicName), any());
        verify(kafkaTopic, times(2)).getCourseForumGroup();
    }

    @Test
    void sendCourseForumGroupCreatedEvents_EmptyList() {
        String topicName = "course-forum-group-topic";
        
        when(kafkaTopic.getCourseForumGroup()).thenReturn(topicName);
        
        producer.sendCourseForumGroupCreatedEvents(List.of());
        
        verify(kafkaTemplate, never()).send(anyString(), any());
    }

    @Test
    void sendCourseForumGroupCreatedEvents_NullList() {
        assertThrows(NullPointerException.class, 
            () -> producer.sendCourseForumGroupCreatedEvents(null));
    }

    @Test
    void sendCourseForumGroupCreatedEvents_WithNullElements() {
        String topicName = "course-forum-group-topic";

        List<Object> messages = new ArrayList<>();
        messages.add("valid-message");
        messages.add(null);

        when(kafkaTopic.getCourseForumGroup()).thenReturn(topicName);

        assertThrows(NullPointerException.class,
                () -> producer.sendCourseForumGroupCreatedEvents(messages));
    }

    @Test
    void getTopic_ReturnsCorrectTopic() {
        String expectedTopic = "course-forum-group-topic";
        when(kafkaTopic.getCourseForumGroup()).thenReturn(expectedTopic);
        
        String actualTopic = producer.getTopic();
        
        assertEquals(expectedTopic, actualTopic);
        verify(kafkaTopic, times(1)).getCourseForumGroup();
    }
}