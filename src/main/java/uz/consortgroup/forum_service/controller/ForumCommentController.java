package uz.consortgroup.forum_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uz.consortgroup.core.api.v1.dto.forum.CreateForumCommentRequest;
import uz.consortgroup.core.api.v1.dto.forum.ForumCommentResponse;
import uz.consortgroup.forum_service.service.service.ForumCommentService;

@RestController
@RequestMapping("/api/v1/forum/forum-comment")
@RequiredArgsConstructor
@Validated
public class ForumCommentController {
    private final ForumCommentService forumCommentService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ForumCommentResponse createComment(@Valid @RequestBody CreateForumCommentRequest request) {
        return forumCommentService.createComment(request);
    }
}
