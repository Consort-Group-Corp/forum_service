package uz.consortgroup.forum_service.service.service;

import uz.consortgroup.core.api.v1.dto.forum.CreateForumTopicRequest;
import uz.consortgroup.core.api.v1.dto.forum.ForumTopicResponse;
import uz.consortgroup.forum_service.entity.ForumTopic;

import java.util.UUID;

public interface ForumTopicService {
    ForumTopicResponse createForumTopic(UUID forumId, CreateForumTopicRequest request);
    ForumTopic findForumTopicById(UUID topicId);
}
