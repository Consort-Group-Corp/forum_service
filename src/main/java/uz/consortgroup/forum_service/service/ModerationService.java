package uz.consortgroup.forum_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.consortgroup.core.api.v1.dto.forum.moderation.UserBlockCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.moderation.UserMuteCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.moderation.response.UserBlockResponseDto;
import uz.consortgroup.core.api.v1.dto.forum.moderation.response.UserMuteResponseDto;

import java.util.UUID;

public interface ModerationService {
    UserBlockResponseDto blockUser(UserBlockCreateRequestDto dto);
    void unblockUser(UUID blockId);
    boolean isUserBlocked(UUID userId);

    UserMuteResponseDto muteUser(UserMuteCreateRequestDto dto);
    void unmuteUser(UUID muteId);
    boolean isUserMuted(UUID userId);

    Page<UserMuteResponseDto> getMutedUsers(Pageable pageable);
    Page<UserBlockResponseDto> getBlockedUsers(Pageable pageable);
}
