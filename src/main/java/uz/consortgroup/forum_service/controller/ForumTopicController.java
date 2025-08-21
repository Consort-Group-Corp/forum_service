package uz.consortgroup.forum_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uz.consortgroup.core.api.v1.dto.forum.CreateForumTopicRequest;
import uz.consortgroup.core.api.v1.dto.forum.ForumTopicResponse;
import uz.consortgroup.forum_service.service.service.ForumTopicService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/forum/forum-topic")
@RequiredArgsConstructor
@Validated
public class ForumTopicController {
    private final ForumTopicService forumTopicService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{forumId}/topics")
    public ForumTopicResponse createForumTopic(@PathVariable UUID forumId,
                                               @Valid @RequestBody CreateForumTopicRequest request) {
        return forumTopicService.createForumTopic(forumId, request);
    }
}
