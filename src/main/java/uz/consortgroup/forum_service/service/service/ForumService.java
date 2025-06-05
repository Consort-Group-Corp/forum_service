package uz.consortgroup.forum_service.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.forum_service.entity.Forum;
import uz.consortgroup.forum_service.event.coursegroup.CourseGroupOpenedEvent;
import uz.consortgroup.forum_service.repository.ForumRepository;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForumService {
    private final ForumRepository forumRepository;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    public void createForum(List<CourseGroupOpenedEvent> events) {
        if (events.isEmpty()) {
            return;
        }

        List<Forum> forums = events.stream()
                .filter(event -> markIfNotProcessed(event.getMessageId()))
                .map(event -> Forum.builder()
                        .courseId(event.getCourseId())
                        .groupId(event.getGroupId())
                        .title(event.getCourseTitle())
                        .startTime(event.getStartTime())
                        .endTime(event.getEndTime())
                        .createdAt(event.getCreatedAt())
                        .build())
                .filter(Objects::nonNull)
                .toList();

        try {
            forumRepository.saveAll(forums);
        } catch (Exception e) {
            log.error("Error creating forums", e);
            throw new RuntimeException("Error creating forums", e);
        }
    }

    private boolean markIfNotProcessed(UUID messageId) {
        String key = "event_processed:" + messageId;
        Boolean wasSet = redisTemplate.opsForValue()
                .setIfAbsent(key, "true", Duration.ofHours(1));
        return Boolean.TRUE.equals(wasSet);
    }
}
