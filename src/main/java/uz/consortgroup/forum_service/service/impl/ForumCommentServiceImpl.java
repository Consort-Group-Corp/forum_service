package uz.consortgroup.forum_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumCommentCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumCommentUpdateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.response.ForumCommentResponseDto;
import uz.consortgroup.forum_service.entity.ForumComment;
import uz.consortgroup.forum_service.entity.ForumTopic;
import uz.consortgroup.forum_service.exception.ForumCommentNotFoundException;
import uz.consortgroup.forum_service.mapper.ForumCommentMapper;
import uz.consortgroup.forum_service.repository.ForumCommentRepository;
import uz.consortgroup.forum_service.security.AuthContext;
import uz.consortgroup.forum_service.service.ForumCommentService;
import uz.consortgroup.forum_service.validator.ForumCommentValidator;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ForumCommentServiceImpl implements ForumCommentService {

    private final ForumCommentRepository commentRepository;
    private final ForumCommentMapper commentMapper;
    private final ForumCommentValidator commentValidator;
    private final AuthContext authContext;

    @Override
    @Transactional
    public ForumCommentResponseDto createComment(UUID topicId, ForumCommentCreateRequestDto dto) {
        UUID authorId = authContext.getCurrentUserId();

        log.info("Creating comment in topicId={} by authorId={}", topicId, authorId);

        ForumTopic topic = commentValidator.validateCreate(topicId, dto, Instant.now());
        ForumComment comment = commentMapper.toEntityOnCreate(dto);

        comment.setAuthorId(authorId);
        comment.setForumTopic(topic);
        comment.setCreatedAt(Instant.now());

        ForumComment saved = commentRepository.save(comment);

        return commentMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ForumCommentResponseDto updateComment(UUID commentId, ForumCommentUpdateRequestDto dto) {
        log.info("Updating comment id={}", commentId);

        ForumComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ForumCommentNotFoundException(String.format("Forum comment with id %s not found", commentId)));

        commentValidator.validateUpdate(comment.getForumTopic(), dto, Instant.now());

        ForumComment updated = commentMapper.updateEntityFromDto(dto, comment);
        updated.setUpdatedAt(Instant.now());

        ForumComment saved = commentRepository.save(updated);

        return commentMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public ForumCommentResponseDto getCommentById(UUID commentId) {
        log.info("Fetching comment id={}", commentId);

        ForumComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ForumCommentNotFoundException(String.format("Forum comment with id %s not found", commentId)));
        return commentMapper.toDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ForumCommentResponseDto> getTopicComments(UUID topicId, Pageable pageable) {

        return commentRepository.findByForumTopic_IdOrderByCreatedAtAsc(topicId, pageable)
                .map(commentMapper::toDto);
    }
}
