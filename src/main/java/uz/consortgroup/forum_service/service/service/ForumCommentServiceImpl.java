package uz.consortgroup.forum_service.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.forum.CreateForumCommentRequest;
import uz.consortgroup.core.api.v1.dto.forum.ForumCommentResponse;
import uz.consortgroup.forum_service.checker.ForumAccessChecker;
import uz.consortgroup.forum_service.entity.ForumComment;
import uz.consortgroup.forum_service.entity.ForumTopic;
import uz.consortgroup.forum_service.mapper.ForumCommentMapper;
import uz.consortgroup.forum_service.repository.ForumCommentRepository;
import uz.consortgroup.forum_service.repository.IdCount;
import uz.consortgroup.forum_service.security.AuthenticatedUser;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForumCommentServiceImpl implements ForumCommentService {

    private final ForumTopicService forumTopicService;
    private final ForumCommentRepository forumCommentRepository;
    private final ForumCommentMapper forumCommentMapper;
    private final ForumAccessChecker forumAccessChecker;
    private final ForumService forumService;

    @Override
    @Transactional
    public ForumCommentResponse createComment(UUID topicId, CreateForumCommentRequest request) {
        AuthenticatedUser user = (AuthenticatedUser) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        UUID authorId = user.getUserId();

        log.info("Creating comment for topicId={}, authorId={}", topicId, authorId);

        ForumTopic topic = forumTopicService.findForumTopicById(topicId);
        UUID groupId = topic.getForum().getGroupId();
        forumAccessChecker.checkAccessOrThrow(authorId, groupId);

        ForumComment comment = ForumComment.builder()
                .forumTopic(topic)
                .forum(topic.getForum())
                .authorId(authorId)
                .content(request.getContent())
                .createdAt(Instant.now())
                .build();

        forumCommentRepository.save(comment);

        log.info("Comment created. commentId={}, topicId={}, forumId={}, groupId={}",
                comment.getId(), topic.getId(), topic.getForum().getId(), groupId);

        return forumCommentMapper.toDto(comment);
    }

    @Override
    public Map<UUID, Long> countByForumIds(List<UUID> forumIds) {
        log.info("Counting comments by forumIds={}", forumIds);

        if (forumIds.isEmpty()) return Map.of();
        return forumCommentRepository.countCommentsByForumIds(forumIds).stream()
                .collect(Collectors.toMap(IdCount::getId, IdCount::getCount));
    }

    @Override
    public Long getTotalCommentsCount() {
       log.info("Counting all comments");
       return forumCommentRepository.countAllComments();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getCommentsCountByForumId(UUID forumId) {
        log.info("Counting comments by forumId={}", forumId);

        forumService.findForumById(forumId);

        log.info("Forum found. forumId={}", forumId);

        return forumCommentRepository.countByForumId(forumId);

    }
}
