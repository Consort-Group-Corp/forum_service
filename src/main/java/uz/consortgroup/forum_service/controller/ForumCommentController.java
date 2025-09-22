package uz.consortgroup.forum_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import uz.consortgroup.core.api.v1.dto.forum.moderation.response.UserBlockListItemResponseDto;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumCommentCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumCommentUpdateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.response.ForumCommentResponseDto;
import uz.consortgroup.forum_service.service.ForumCommentService;
import uz.consortgroup.forum_service.service.ModerationQueryService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/forum/forum-comment")
@RequiredArgsConstructor
public class ForumCommentController {

    private final ForumCommentService commentService;
    private final ModerationQueryService moderationQueryService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/topic/{topicId}")
    public ForumCommentResponseDto createComment(@PathVariable UUID topicId,
                                                 @Valid @RequestBody ForumCommentCreateRequestDto dto) {
        return commentService.createComment(topicId, dto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{commentId}")
    public ForumCommentResponseDto updateComment(@PathVariable UUID commentId,
                                                 @Valid @RequestBody ForumCommentUpdateRequestDto dto) {
        return commentService.updateComment(commentId, dto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{commentId}")
    public ForumCommentResponseDto getCommentById(@PathVariable UUID commentId) {
        return commentService.getCommentById(commentId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/by-topic/{topicId}")
    public Page<ForumCommentResponseDto> getTopicComments(@PathVariable UUID topicId, Pageable pageable) {
        return commentService.getTopicComments(topicId, pageable);
    }

    @GetMapping("/blocks")
    @ResponseStatus(HttpStatus.OK)
    public Page<UserBlockListItemResponseDto> listBlocks(@RequestParam UUID forumId, Pageable pageable) {
        return moderationQueryService.listBlocks(forumId, pageable);
    }
}
