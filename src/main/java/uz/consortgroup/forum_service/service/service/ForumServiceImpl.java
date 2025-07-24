package uz.consortgroup.forum_service.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.forum_service.entity.Forum;
import uz.consortgroup.forum_service.event.course_group.CourseGroupOpenedEvent;
import uz.consortgroup.forum_service.repository.ForumRepository;
import uz.consortgroup.forum_service.service.event.CourseForumGroupEventService;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForumServiceImpl implements ForumService {
    private final ForumRepository forumRepository;
    private final StringRedisTemplate redisTemplate;
    private final CourseForumGroupEventService courseForumGroupEventService;

    @Transactional
    @Override
    public void createForum(List<CourseGroupOpenedEvent> events) {
        if (events.isEmpty()) {
            log.warn("Received empty list of CourseGroupOpenedEvent. Skipping forum creation.");
            return;
        }

        log.info("Processing {} CourseGroupOpenedEvent(s) for forum creation.", events.size());

        List<Forum> forums = events.stream()
                .filter(Objects::nonNull)
                .filter(event -> {
                    boolean shouldProcess = markIfNotProcessed(event.getMessageId());
                    if (!shouldProcess) {
                        log.info("Skipping duplicate or already processed event with messageId={}", event.getMessageId());
                    }
                    return shouldProcess;
                })
                .map(this::mapToForum)
                .filter(Objects::nonNull)
                .toList();

        if (forums.isEmpty()) {
            log.info("No new forums to create after filtering.");
            return;
        }

        forumRepository.saveAll(forums);
        log.info("Successfully saved {} forum(s) to the database.", forums.size());

        courseForumGroupEventService.sendCourseForumGroupCreatedEvent(forums.getFirst());
        log.info("Sent CourseForumGroupCreatedEvent for courseId={}, groupId={}",
                forums.getFirst().getCourseId(), forums.getFirst().getGroupId());
    }

    private Forum mapToForum(CourseGroupOpenedEvent event) {
        log.debug("Mapping CourseGroupOpenedEvent to Forum entity. courseId={}, groupId={}",
                event.getCourseId(), event.getGroupId());

        return Forum.builder()
                .courseId(event.getCourseId())
                .groupId(event.getGroupId())
                .title(event.getCourseTitle())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .createdAt(event.getCreatedAt())
                .build();
    }

    private boolean markIfNotProcessed(UUID messageId) {
        String key = "event_processed:" + messageId;
        Boolean wasSet = redisTemplate.opsForValue().setIfAbsent(key, "true", Duration.ofHours(1));
        log.debug("Event deduplication check for messageId={}: {}", messageId, wasSet);
        return Boolean.TRUE.equals(wasSet);
    }
}
