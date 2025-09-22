package uz.consortgroup.forum_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.consortgroup.core.api.v1.dto.forum.enumeration.LanguageCode;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumTopicCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumTopicUpdateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.response.ForumTopicResponseDto;


import java.util.UUID;

public interface ForumTopicService {
    ForumTopicResponseDto createForumTopic(UUID forumId, ForumTopicCreateRequestDto dto);
    ForumTopicResponseDto updateForumTopic(UUID forumId, ForumTopicUpdateRequestDto dto);
    ForumTopicResponseDto getForumTopicById(UUID forumId);
    Page<ForumTopicResponseDto> getForumTopics(UUID forumId, LanguageCode lang, UUID lessonId, Pageable pageable);
}
