package uz.consortgroup.forum_service.service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import uz.consortgroup.forum_service.entity.Forum;
import uz.consortgroup.forum_service.event.course_group.CourseGroupOpenedEvent;
import uz.consortgroup.forum_service.repository.ForumRepository;
import uz.consortgroup.forum_service.service.event.CourseForumGroupEventService;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ForumServiceImplTest {

    @Mock
    private ForumRepository forumRepository;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private CourseForumGroupEventService eventService;

    @InjectMocks
    private ForumServiceImpl forumService;

    @Test
    void createForum_Success() {
        UUID messageId = UUID.randomUUID();
        CourseGroupOpenedEvent event = createTestEvent(messageId);
        
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class)))
            .thenReturn(true);

        forumService.createForum(List.of(event));

        verify(forumRepository).saveAll(anyList());
        verify(eventService).sendCourseForumGroupCreatedEvent(any(Forum.class));
    }

    @Test
    void createForum_EmptyList() {
        forumService.createForum(List.of());
        
        verify(forumRepository, never()).saveAll(any());
        verify(eventService, never()).sendCourseForumGroupCreatedEvent(any());
    }

    @Test
    void createForum_DuplicateEvent() {
        UUID messageId = UUID.randomUUID();
        CourseGroupOpenedEvent event = createTestEvent(messageId);
        
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class)))
            .thenReturn(false);

        forumService.createForum(List.of(event));

        verify(forumRepository, never()).saveAll(any());
        verify(eventService, never()).sendCourseForumGroupCreatedEvent(any());
    }

    @Test
    void createForum_NullInList() {
        UUID messageId = UUID.randomUUID();
        CourseGroupOpenedEvent event = createTestEvent(messageId);

        List<CourseGroupOpenedEvent> events = new ArrayList<>();
        events.add(event);
        events.add(null);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class)))
                .thenReturn(true);

        List<Forum> savedForums = new ArrayList<>();
        when(forumRepository.saveAll(anyList())).thenAnswer(inv -> {
            savedForums.addAll(inv.getArgument(0));
            return null;
        });

        forumService.createForum(events);

        assertEquals(1, savedForums.size());
        verify(eventService).sendCourseForumGroupCreatedEvent(any(Forum.class));
    }

    @Test
    void createForum_RedisError() {
        UUID messageId = UUID.randomUUID();
        CourseGroupOpenedEvent event = createTestEvent(messageId);
        
        when(redisTemplate.opsForValue()).thenThrow(new RuntimeException("Redis error"));

        assertThrows(RuntimeException.class, () -> 
            forumService.createForum(List.of(event)));
    }

    private CourseGroupOpenedEvent createTestEvent(UUID messageId) {
        return CourseGroupOpenedEvent.builder()
            .messageId(messageId)
            .courseId(UUID.randomUUID())
            .groupId(UUID.randomUUID())
            .courseTitle("Test Course")
            .startTime(Instant.now())
            .endTime(Instant.now().plusSeconds(3600))
            .createdAt(Instant.now())
            .build();
    }
}