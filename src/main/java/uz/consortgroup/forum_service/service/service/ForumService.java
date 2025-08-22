package uz.consortgroup.forum_service.service.service;

import uz.consortgroup.forum_service.entity.Forum;
import uz.consortgroup.forum_service.event.course_group.CourseGroupOpenedEvent;

import java.util.List;
import java.util.UUID;

public interface ForumService {
    void createForum(List<CourseGroupOpenedEvent> events);
    Forum findForumById(UUID forumId);
}
