package uz.consortgroup.forum_service.service.service;

import uz.consortgroup.core.api.v1.dto.forum.CreateForumCommentRequest;
import uz.consortgroup.core.api.v1.dto.forum.ForumCommentResponse;

public interface ForumCommentService {
    ForumCommentResponse createComment(CreateForumCommentRequest request);
}
