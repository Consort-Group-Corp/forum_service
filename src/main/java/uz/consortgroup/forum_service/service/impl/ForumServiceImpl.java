package uz.consortgroup.forum_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumUpdateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.response.ForumResponseDto;
import uz.consortgroup.forum_service.entity.Forum;
import uz.consortgroup.forum_service.entity.ForumUserGroup;
import uz.consortgroup.forum_service.exception.ForumAlreadyExistsException;
import uz.consortgroup.forum_service.exception.ForumNotFoundException;
import uz.consortgroup.forum_service.mapper.ForumMapper;
import uz.consortgroup.forum_service.repository.ForumRepository;
import uz.consortgroup.forum_service.security.AuthContext;
import uz.consortgroup.forum_service.service.ForumService;
import uz.consortgroup.forum_service.validator.ForumValidator;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ForumServiceImpl implements ForumService {

    private final ForumRepository forumRepository;
    private final ForumValidator forumValidator;
    private final ForumMapper forumMapper;
    private final AuthContext authContext;

    @Override
    @Transactional
    public ForumResponseDto createForum(ForumCreateRequestDto dto) {
        UUID ownerId = authContext.getCurrentUserId();

        log.info("Creating forum: courseId={}, ownerId={}, policy={}",
                dto.getCourseId(), ownerId, dto.getForumAccessPolicy());

        forumRepository.findByCourseId(dto.getCourseId()).ifPresent(existing -> {
            throw new ForumAlreadyExistsException(String.format("Forum for course %s already exists", dto.getCourseId()));
        });

        ForumUserGroup group = forumValidator.validateCreateAndResolveGroup(dto);


        Forum forum = forumMapper.toEntityOnCreate(dto);
        forum.setOwnerId(ownerId);
        forum.setCreatedAt(Instant.now());
        forum.setGroup(group);

        Forum saved = forumRepository.save(forum);
        ForumResponseDto response = forumMapper.toDto(saved);

        log.info("Forum created: id={}, courseId={}", saved.getId(), saved.getCourseId());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ForumResponseDto getForumById(UUID forumId) {
        log.info("Fetching forum by id={}", forumId);

        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() ->
                        new ForumNotFoundException(String.format("Forum with id %s not found", forumId)));

        return forumMapper.toDto(forum);
    }

    @Override
    @Transactional
    public ForumResponseDto updateForumById(UUID forumId, ForumUpdateRequestDto dto) {
        log.info("Updating forum: id={}, policy={}, groupId={}",
                forumId, dto.getForumAccessPolicy(), dto.getGroupId());

        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() ->
                        new ForumNotFoundException(String.format("Forum with id %s not found", forumId)));


        ForumUserGroup resolvedGroup = forumValidator.validateUpdateAndResolveGroup(forum, dto);

        Forum updated = forumMapper.updateEntityFromDto(dto, forum);

        updated.setGroup(resolvedGroup);

        Forum saved = forumRepository.save(updated);
        ForumResponseDto response = forumMapper.toDto(saved);

        log.info("Forum updated: id={}", saved.getId());
        return response;
    }

    @Override
    public Page<ForumResponseDto> findForumByTitle(String title, Pageable pageable) {
        log.info("Find forum by title={}, pageable={}", title, pageable);

        Page<Forum> page = forumRepository.searchByTitle(title, pageable);

        return page.map(forumMapper::toDto);
    }
}
