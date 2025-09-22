package uz.consortgroup.forum_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.forum.enumeration.LanguageCode;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumTopicCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumTopicUpdateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.response.ForumTopicResponseDto;
import uz.consortgroup.forum_service.entity.Forum;
import uz.consortgroup.forum_service.entity.ForumTopic;
import uz.consortgroup.forum_service.exception.ForumTopicNotFoundException;
import uz.consortgroup.forum_service.mapper.ForumTopicMapper;
import uz.consortgroup.forum_service.repository.ForumTopicRepository;
import uz.consortgroup.forum_service.security.AuthContext;
import uz.consortgroup.forum_service.service.ForumTopicService;
import uz.consortgroup.forum_service.validator.ForumTopicValidator;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ForumTopicServiceImpl implements ForumTopicService {

    private final ForumTopicRepository forumTopicRepository;
    private final ForumTopicMapper forumTopicMapper;
    private final ForumTopicValidator forumTopicValidator;
    private final AuthContext authContext;

    @Override
    @Transactional
    public ForumTopicResponseDto createForumTopic(UUID forumId, ForumTopicCreateRequestDto dto) {
        UUID authorId = authContext.getCurrentUserId();

        log.info("Creating forum topic in forumId={} by authorId={}", forumId, authorId);

        Forum forum = forumTopicValidator.validateCreate(forumId, dto, Instant.now());
        ForumTopic topic = forumTopicMapper.toEntityOnCreate(dto);
        topic.setForum(forum);
        topic.setAuthorId(authorId);
        topic.setCreatedAt(Instant.now());

        ForumTopic saved = forumTopicRepository.save(topic);

        return forumTopicMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ForumTopicResponseDto updateForumTopic(UUID forumId, ForumTopicUpdateRequestDto dto) {
        log.info("Updating forum topic id={}", forumId);

        ForumTopic topic = forumTopicRepository.findById(forumId)
                .orElseThrow(() -> new ForumTopicNotFoundException(
                        String.format("Forum topic with id %s not found", forumId)));

        forumTopicValidator.validateUpdate(topic.getForum(), dto, Instant.now());

        ForumTopic updated = forumTopicMapper.updateEntityFromDto(dto, topic);
        updated.setUpdatedAt(Instant.now());

        ForumTopic saved = forumTopicRepository.save(updated);
        return forumTopicMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ForumTopicResponseDto getForumTopicById(UUID forumId) {
        log.info("Fetching forum topic id={}", forumId);

        ForumTopic topic = forumTopicRepository.findById(forumId)
                .orElseThrow(() -> new ForumTopicNotFoundException(
                        String.format("Forum topic with id %s not found", forumId)));

        return forumTopicMapper.toDto(topic);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ForumTopicResponseDto> getForumTopics(UUID forumId, LanguageCode lang, UUID lessonId, Pageable pageable) {
        log.info("Listing topics: forumId={}, lang={}, lessonId={}", forumId, lang, lessonId);

        Page<ForumTopic> page = forumTopicRepository.findByFilters(forumId, lang, lessonId, pageable);

        return page.map(forumTopicMapper::toDto);
    }
}
