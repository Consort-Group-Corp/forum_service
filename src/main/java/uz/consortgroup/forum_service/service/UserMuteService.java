package uz.consortgroup.forum_service.service;

import uz.consortgroup.core.api.v1.dto.forum.moderation.UserMuteCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.moderation.response.UserMuteResponseDto;

import java.util.UUID;

public interface UserMuteService {
    UserMuteResponseDto muteUser(UserMuteCreateRequestDto dto);
    void unmuteUser(UUID muteId);
    boolean isUserMuted(UUID userId);
    void unmuteUserByUserId(UUID userId);
}
