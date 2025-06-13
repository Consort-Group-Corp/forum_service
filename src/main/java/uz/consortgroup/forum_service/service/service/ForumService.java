package uz.consortgroup.forum_service.service.service;

import uz.consortgroup.forum_service.event.course_group.CourseGroupOpenedEvent;

import java.util.List;

public interface ForumService {
    void createForum(List<CourseGroupOpenedEvent> events);
}
