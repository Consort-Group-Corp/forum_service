package uz.consortgroup.forum_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumCommentCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumCommentUpdateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.response.ForumCommentResponseDto;

import java.util.UUID;

public interface ForumCommentService {
    ForumCommentResponseDto createComment(UUID topicId, ForumCommentCreateRequestDto dto);
    ForumCommentResponseDto updateComment(UUID commentId, ForumCommentUpdateRequestDto dto);
    ForumCommentResponseDto getCommentById(UUID commentId);
    Page<ForumCommentResponseDto> getTopicComments(UUID topicId, Pageable pageable);

}
