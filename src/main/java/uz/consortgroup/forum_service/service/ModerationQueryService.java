package uz.consortgroup.forum_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.consortgroup.core.api.v1.dto.forum.moderation.response.UserBlockListItemResponseDto;

import java.util.UUID;

public interface ModerationQueryService {
    Page<UserBlockListItemResponseDto> listBlocks(UUID forumId, Pageable pageable);
}
