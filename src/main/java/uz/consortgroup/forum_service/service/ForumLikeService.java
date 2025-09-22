package uz.consortgroup.forum_service.service;

import uz.consortgroup.core.api.v1.dto.forum.response.ForumLikeResponseDto;

import java.util.UUID;

public interface ForumLikeService {
    ForumLikeResponseDto addLikeToForum(UUID forumId);
    ForumLikeResponseDto removeLikeFromForum(UUID forumId);
    ForumLikeResponseDto getForumLikeStatus(UUID forumId);
}
