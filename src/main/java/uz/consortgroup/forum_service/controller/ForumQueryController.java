package uz.consortgroup.forum_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uz.consortgroup.core.api.v1.dto.forum.ForumListParamsRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.ForumListResponseDto;
import uz.consortgroup.forum_service.service.service.ForumListQueryService;

@RestController
@RequestMapping("/api/v1/forums")
@RequiredArgsConstructor
@Validated
class ForumQueryController {

    private final ForumListQueryService forumListQueryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ForumListResponseDto list(@ModelAttribute @Valid ForumListParamsRequestDto params) {
        return forumListQueryService.list(params);
    }
}