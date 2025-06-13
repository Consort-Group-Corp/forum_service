package uz.consortgroup.forum_service.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.forum.CreateForumCommentRequest;
import uz.consortgroup.core.api.v1.dto.forum.ForumCommentResponse;
import uz.consortgroup.forum_service.asspect.annotation.AllAspect;
import uz.consortgroup.forum_service.checker.ForumAccessChecker;
import uz.consortgroup.forum_service.entity.ForumComment;
import uz.consortgroup.forum_service.entity.ForumTopic;
import uz.consortgroup.forum_service.mapper.ForumCommentMapper;
import uz.consortgroup.forum_service.repository.ForumCommentRepository;
import uz.consortgroup.forum_service.security.JwtUserDetails;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ForumCommentServiceImpl implements ForumCommentService {
    private final ForumTopicService forumTopicService;
    private final ForumCommentRepository forumCommentRepository;
    private final ForumCommentMapper forumCommentMapper;
    private final ForumAccessChecker forumAccessChecker;

    @Override
    @Transactional
    @AllAspect
    public ForumCommentResponse createComment(CreateForumCommentRequest request) {
        JwtUserDetails userDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID authorId = userDetails.getId();

        ForumTopic topic = forumTopicService.findForumTopicById(request.getTopicId());
        forumAccessChecker.checkAccessOrThrow(authorId, topic.getForumId());

        ForumComment comment = ForumComment.builder()
                .forumTopic(topic)
                .authorId(authorId)
                .content(request.getContent())
                .createdAt(Instant.now())
                .build();

        forumCommentRepository.save(comment);

        return forumCommentMapper.toDto(comment);
    }
}
