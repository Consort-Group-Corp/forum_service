package uz.consortgroup.forum_service.service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.forum.CreateForumTopicRequest;
import uz.consortgroup.core.api.v1.dto.forum.ForumTopicResponse;
import uz.consortgroup.forum_service.asspect.annotation.AllAspect;
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
    @AllAspect
    @Transactional
    public ForumTopicResponse createForumTopic(CreateForumTopicRequest request) {
        JwtUserDetails userDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID authorId = userDetails.getId();
        log.info("Author id: {}", authorId);
        log.info("Incoming forumId in CreateForumTopicRequest: {}", request.getForumId());
        forumAccessChecker.checkAccessOrThrow(authorId, request.getForumId());

        ForumTopic forumTopic = ForumTopic.builder()
                .forumId(request.getForumId())
                .authorId(authorId)
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        forumTopicRepository.save(forumTopic);

        return forumTopicMapper.toDto(forumTopic);
    }

    @Override
    @AllAspect
    @Transactional(readOnly = true)
    public ForumTopic findForumTopicById(UUID topicId) {
        return forumTopicRepository.findById(topicId)
                .orElseThrow(() -> new EntityNotFoundException("Forum topic not found"));
    }
}
