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
import uz.consortgroup.forum_service.entity.ForumTopic;
import uz.consortgroup.forum_service.mapper.ForumTopicMapper;
import uz.consortgroup.forum_service.repository.ForumTopicRepository;
import uz.consortgroup.forum_service.security.JwtUserDetails;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForumTopicServiceImpl implements ForumTopicService {
    private final ForumTopicRepository forumTopicRepository;
    private final ForumTopicMapper forumTopicMapper;
    private final ForumAccessChecker forumAccessChecker;

    @Override
    @Transactional
    public ForumTopicResponse createForumTopic(CreateForumTopicRequest request) {
        JwtUserDetails userDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID authorId = userDetails.getId();

        log.info("Creating new forum topic by authorId={}", authorId);
        log.info("Received forumId={} from CreateForumTopicRequest", request.getForumId());

        forumAccessChecker.checkAccessOrThrow(authorId, request.getForumId());

        ForumTopic forumTopic = ForumTopic.builder()
                .forumId(request.getForumId())
                .authorId(authorId)
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        forumTopicRepository.save(forumTopic);
        log.info("Forum topic created successfully with id={}", forumTopic.getId());

        return forumTopicMapper.toDto(forumTopic);
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
