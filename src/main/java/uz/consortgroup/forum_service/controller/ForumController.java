package uz.consortgroup.forum_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumUpdateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.response.ForumResponseDto;
import uz.consortgroup.forum_service.service.ForumService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/forums")
@Validated
public class ForumController {

    private final ForumService forumService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ForumResponseDto createForum(@Valid @RequestBody ForumCreateRequestDto dto) {
        return forumService.createForum(dto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{forumId}")
    public ForumResponseDto getForumById(@PathVariable UUID forumId) {
        return forumService.getForumById(forumId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{forumId}")
    public ForumResponseDto updateForum(@PathVariable UUID forumId, @Valid @RequestBody ForumUpdateRequestDto dto) {
        return forumService.updateForumById(forumId, dto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Page<ForumResponseDto> findForumByTitle(
            @RequestParam String title,
            Pageable pageable) {
        return forumService.findForumByTitle(title, pageable);
    }
}
