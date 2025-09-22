package uz.consortgroup.forum_service.service;

import uz.consortgroup.core.api.v1.dto.forum.moderation.UserBlockCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.moderation.response.UserBlockResponseDto;

import java.util.UUID;

public interface UserBlockService {
    UserBlockResponseDto blockUser(UserBlockCreateRequestDto dto);
    void unblockUser(UUID blockId);
    boolean isUserBlocked(UUID userId);
    void unblockUserByUserId(UUID userId);
}
