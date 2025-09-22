package uz.consortgroup.forum_service.service.impl.moderation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.consortgroup.core.api.v1.dto.forum.moderation.UserBlockCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.moderation.UserMuteCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.moderation.response.UserBlockResponseDto;
import uz.consortgroup.core.api.v1.dto.forum.moderation.response.UserMuteResponseDto;
import uz.consortgroup.forum_service.security.AuthContext;
import uz.consortgroup.forum_service.service.ModerationService;
import uz.consortgroup.forum_service.service.UserBlockService;
import uz.consortgroup.forum_service.service.UserMuteService;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModerationServiceImpl implements ModerationService {

    private final UserBlockService userBlockService;
    private final UserMuteService userMuteService;
    private final AuthContext authContext;

    @Override
    public UserBlockResponseDto blockUser(UserBlockCreateRequestDto dto) {
        UUID moderatorId = authContext.getCurrentUserId();

        log.info("ModerationService: Blocking user. userId={}, issuedBy={}, reason={}",
                dto.getUserId(), moderatorId, dto.getReason());

        UserBlockResponseDto response = userBlockService.blockUser(dto);

        log.info("ModerationService: User blocked successfully. blockId={}", response.getId());
        return response;
    }

    @Override
    public void unblockUser(UUID blockId) {
        log.info("ModerationService: Unblocking user. blockId={}", blockId);

        userBlockService.unblockUser(blockId);

        log.info("ModerationService: User unblocked successfully. blockId={}", blockId);
    }

    @Override
    public boolean isUserBlocked(UUID userId) {
        log.debug("ModerationService: Checking if user is blocked. userId={}", userId);

        boolean isBlocked = userBlockService.isUserBlocked(userId);

        log.debug("ModerationService: User blocked status. userId={}, isBlocked={}", userId, isBlocked);
        return isBlocked;
    }

    @Override
    public UserMuteResponseDto muteUser(UserMuteCreateRequestDto dto) {
        UUID moderatorId = authContext.getCurrentUserId();

        log.info("ModerationService: Muting user. userId={}, issuedBy={}, reason={}, muteUntil={}",
                dto.getUserId(), moderatorId, dto.getReason(), dto.getMuteUntil());

        UserMuteResponseDto response = userMuteService.muteUser(dto);

        log.info("ModerationService: User muted successfully. muteId={}", response.getId());
        return response;
    }

    @Override
    public void unmuteUser(UUID muteId) {
        log.info("ModerationService: Unmuting user. muteId={}", muteId);

        userMuteService.unmuteUser(muteId);

        log.info("ModerationService: User unmuted successfully. muteId={}", muteId);
    }

    @Override
    public boolean isUserMuted(UUID userId) {
        log.debug("ModerationService: Checking if user is muted. userId={}", userId);

        boolean isMuted = userMuteService.isUserMuted(userId);

        log.debug("ModerationService: User muted status. userId={}, isMuted={}", userId, isMuted);
        return isMuted;
    }

    @Override
    public Page<UserMuteResponseDto> getMutedUsers(Pageable pageable) {
        return null;
    }

    @Override
    public Page<UserBlockResponseDto> getBlockedUsers(Pageable pageable) {
        return null;
    }
}