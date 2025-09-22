package uz.consortgroup.forum_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumUpdateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.response.ForumResponseDto;

import java.util.UUID;

public interface ForumService {
    ForumResponseDto createForum(ForumCreateRequestDto dto);
    ForumResponseDto getForumById(UUID forumId);
    ForumResponseDto updateForumById(UUID forumId, ForumUpdateRequestDto dto);
    Page<ForumResponseDto> findForumByTitle(String title, Pageable pageable);
}
