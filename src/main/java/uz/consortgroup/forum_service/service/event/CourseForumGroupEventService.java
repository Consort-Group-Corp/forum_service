package uz.consortgroup.forum_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.consortgroup.forum_service.entity.Forum;
import uz.consortgroup.forum_service.event.course_group.CourseForumGroupCreatedEvent;
import uz.consortgroup.forum_service.kafka.CourseForumGroupEventProducer;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseForumGroupEventService {
    private final CourseForumGroupEventProducer courseForumGroupEventProducer;

    public void sendCourseForumGroupCreatedEvent(Forum forum) {
        CourseForumGroupCreatedEvent event = CourseForumGroupCreatedEvent.builder()
                .messageId(UUID.randomUUID())
                .courseId(forum.getCourseId())
                .groupId(forum.getGroupId())
                .startTime(forum.getStartTime())
                .endTime(forum.getEndTime())
                .createdAt(forum.getCreatedAt())
                .build();

        courseForumGroupEventProducer.sendCourseForumGroupCreatedEvents(List.of(event));
    }
}
