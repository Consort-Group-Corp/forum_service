package uz.consortgroup.forum_service.service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.forum.CreateForumTopicRequest;
import uz.consortgroup.core.api.v1.dto.forum.ForumTopicResponse;
import uz.consortgroup.forum_service.checker.ForumAccessChecker;
import uz.consortgroup.forum_service.entity.Forum;
import uz.consortgroup.forum_service.entity.ForumTopic;
import uz.consortgroup.forum_service.mapper.ForumTopicMapper;
import uz.consortgroup.forum_service.repository.ForumTopicRepository;
import uz.consortgroup.forum_service.security.AuthenticatedUser;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForumTopicServiceImpl implements ForumTopicService {
    private final ForumTopicRepository forumTopicRepository;
    private final ForumTopicMapper forumTopicMapper;
    private final ForumService forumService;
    private final ForumAccessChecker forumAccessChecker;

    @Override
    @Transactional
    public ForumTopicResponse createForumTopic(UUID forumId, CreateForumTopicRequest request) {
        AuthenticatedUser user = (AuthenticatedUser) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        UUID authorId = user.getUserId();

        log.info("Creating new forum topic. forumId={}, authorId={}", forumId, authorId);

        Forum forum  = forumService.findForumById(forumId);
        UUID groupId = forum.getGroupId();

        forumAccessChecker.checkAccessOrThrow(authorId, groupId);

        ForumTopic topic = ForumTopic.builder()
                .forum(forum)
                .authorId(authorId)
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        forumTopicRepository.save(topic);
        log.info("Forum topic created. topicId={}, forumId={}", topic.getId(), forumId);

        return forumTopicMapper.toDto(topic);
    }

    @Override
    @Transactional(readOnly = true)
    public ForumTopic findForumTopicById(UUID topicId) {
        log.debug("Fetching forum topic by id={}", topicId);
        return forumTopicRepository.findById(topicId)
                .orElseThrow(() -> {
                    log.warn("Forum topic with id={} not found", topicId);
                    return new EntityNotFoundException("Forum topic not found");
                });
    }
}
