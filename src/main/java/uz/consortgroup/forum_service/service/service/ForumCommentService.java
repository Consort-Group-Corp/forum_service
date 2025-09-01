package uz.consortgroup.forum_service.service.service;

import uz.consortgroup.core.api.v1.dto.forum.CreateForumCommentRequest;
import uz.consortgroup.core.api.v1.dto.forum.ForumCommentResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ForumCommentService {
    ForumCommentResponse createComment(UUID topicId, CreateForumCommentRequest request);
    Map<UUID, Long> countByForumIds(List<UUID> forumIds);
    Long getTotalCommentsCount();
    Long getCommentsCountByForumId(UUID forumId);
}
