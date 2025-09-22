package uz.consortgroup.forum_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.forum.moderation.UserMuteCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.moderation.response.UserMuteResponseDto;
import uz.consortgroup.forum_service.entity.ForumUserMute;
import uz.consortgroup.forum_service.exception.ForumValidationException;
import uz.consortgroup.forum_service.mapper.ModerationMapper;
import uz.consortgroup.forum_service.repository.ForumUserMuteRepository;
import uz.consortgroup.forum_service.security.AuthContext;
import uz.consortgroup.forum_service.service.UserMuteService;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserMuteServiceImpl implements UserMuteService {
    
    private final ForumUserMuteRepository forumUserMuteRepository;
    private final ModerationMapper moderationMapper;
    private final AuthContext authContext;

    @Transactional
    public UserMuteResponseDto muteUser(UserMuteCreateRequestDto dto) {
        UUID moderatorId = authContext.getCurrentUserId();

        log.info("Muting userId={} until={}", dto.getUserId(), dto.getMuteUntil());

        boolean activeMute = forumUserMuteRepository.existsByUserIdAndMuteUntilAfter(dto.getUserId(), Instant.now());
        if (activeMute) {
            throw new ForumValidationException("User already muted");
        }

        ForumUserMute entity = moderationMapper.toMuteEntity(dto);
        entity.setIssuedBy(moderatorId);
        entity.setCreatedAt(Instant.now());
        ForumUserMute saved = forumUserMuteRepository.save(entity);

        return moderationMapper.toDto(saved);
    }

    @Transactional
    public void unmuteUser(UUID muteId) {
        log.info("Unmuting by muteId={}", muteId);
        forumUserMuteRepository.deleteById(muteId);
    }

    @Transactional(readOnly = true)
    public boolean isUserMuted(UUID userId) {
        return forumUserMuteRepository.existsByUserIdAndMuteUntilAfter(userId, Instant.now());
    }
    
    @Transactional
    public void unmuteUserByUserId(UUID userId) {
        log.info("Unmuting user by userId={}", userId);
        forumUserMuteRepository.deleteById(userId);
    }
}