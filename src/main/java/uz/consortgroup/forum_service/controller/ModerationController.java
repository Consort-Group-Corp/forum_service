package uz.consortgroup.forum_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uz.consortgroup.core.api.v1.dto.forum.moderation.UserBlockCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.moderation.UserMuteCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.moderation.response.UserBlockResponseDto;
import uz.consortgroup.core.api.v1.dto.forum.moderation.response.UserMuteResponseDto;
import uz.consortgroup.forum_service.service.ModerationService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/forum/moderation")
@RequiredArgsConstructor
public class ModerationController {

    private final ModerationService moderationService;

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
}
