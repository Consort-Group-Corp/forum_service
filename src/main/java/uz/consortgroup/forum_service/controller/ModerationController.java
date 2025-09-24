package uz.consortgroup.forum_service.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uz.consortgroup.core.api.v1.dto.forum.moderation.UserBlockCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.moderation.UserMuteCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.moderation.response.UserBlockListItemResponseDto;
import uz.consortgroup.core.api.v1.dto.forum.moderation.response.UserBlockResponseDto;
import uz.consortgroup.core.api.v1.dto.forum.moderation.response.UserMuteResponseDto;
import uz.consortgroup.forum_service.service.ModerationQueryService;
import uz.consortgroup.forum_service.service.ModerationService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/forum/moderation")
@RequiredArgsConstructor
@Tag(name = "Moderation", description = "Модерация пользователей")
public class ModerationController {

    private final ModerationService moderationService;
    private final ModerationQueryService moderationQueryService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/blocks")
    public UserBlockResponseDto blockUser(@Valid @RequestBody UserBlockCreateRequestDto dto) {
        return moderationService.blockUser(dto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/blocks/{blockId}")
    public void unblockUser(@PathVariable UUID blockId) {
        moderationService.unblockUser(blockId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/mutes")
    public UserMuteResponseDto muteUser(@Valid @RequestBody UserMuteCreateRequestDto dto) {
        return moderationService.muteUser(dto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/mutes/{muteId}")
    public void unmuteUser(@PathVariable UUID muteId) {
        moderationService.unmuteUser(muteId);
    }

    @GetMapping("/blocks")
    @ResponseStatus(HttpStatus.OK)
    public Page<UserBlockListItemResponseDto> listBlocks(@RequestParam UUID forumId, Pageable pageable) {
        return moderationQueryService.listBlocks(forumId, pageable);
    }
}
