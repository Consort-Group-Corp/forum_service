package uz.consortgroup.forum_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.forum.moderation.UserBlockCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.moderation.response.UserBlockResponseDto;
import uz.consortgroup.forum_service.entity.ForumUserBlock;
import uz.consortgroup.forum_service.exception.ForumValidationException;
import uz.consortgroup.forum_service.mapper.ModerationMapper;
import uz.consortgroup.forum_service.repository.ForumUserBlockRepository;
import uz.consortgroup.forum_service.security.AuthContext;
import uz.consortgroup.forum_service.service.UserBlockService;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserBlockServiceImpl implements UserBlockService {
    
    private final ForumUserBlockRepository forumUserBlockRepository;
    private final ModerationMapper moderationMapper;
    private final AuthContext authContext;

    @Transactional
    public UserBlockResponseDto blockUser(UserBlockCreateRequestDto dto) {
        UUID moderatorId = authContext.getCurrentUserId();

        log.info("Blocking userId={} by issuedBy={}", dto.getUserId(), moderatorId);

        if (forumUserBlockRepository.existsByUserId(dto.getUserId())) {
            throw new ForumValidationException("User already blocked");
        }

        ForumUserBlock entity = moderationMapper.toBlockEntity(dto);
        entity.setIssuedBy(moderatorId);
        entity.setCreatedAt(Instant.now());
        ForumUserBlock saved = forumUserBlockRepository.save(entity);

        return moderationMapper.toDto(saved);
    }

    @Transactional
    public void unblockUser(UUID blockId) {
        log.info("Unblocking by blockId={}", blockId);
        forumUserBlockRepository.deleteById(blockId);
    }

    @Transactional(readOnly = true)
    public boolean isUserBlocked(UUID userId) {
        return forumUserBlockRepository.existsByUserId(userId);
    }
    
    @Transactional
    public void unblockUserByUserId(UUID userId) {
        log.info("Unblocking user by userId={}", userId);
        forumUserBlockRepository.deleteById(userId);
    }
}