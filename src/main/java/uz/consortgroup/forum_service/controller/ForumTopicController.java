package uz.consortgroup.forum_service.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import uz.consortgroup.core.api.v1.dto.forum.enumeration.LanguageCode;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumTopicCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumTopicUpdateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.response.ForumTopicResponseDto;
import uz.consortgroup.forum_service.service.ForumTopicService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/forum/forum-topic")
@RequiredArgsConstructor
@Tag(name = "Forum Topic", description = "Работа с темами форума")
public class ForumTopicController {

    private final ForumTopicService topicService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{forumId}/create")
    public ForumTopicResponseDto createForumTopic(@PathVariable UUID forumId, @Valid @RequestBody ForumTopicCreateRequestDto dto) {
        return topicService.createForumTopic(forumId, dto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{forumId}")
    public ForumTopicResponseDto updateForumTopic(@PathVariable UUID forumId, @Valid @RequestBody ForumTopicUpdateRequestDto dto) {
        return topicService.updateForumTopic(forumId, dto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{topicId}")
    public ForumTopicResponseDto getForumTopicById(@PathVariable UUID topicId) {
        return topicService.getForumTopicById(topicId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Page<ForumTopicResponseDto> getForumTopics(
            @RequestParam UUID forumId,
            @RequestParam(required = false) LanguageCode lang,
            @RequestParam(required = false) UUID lessonId,
            Pageable pageable
    ) {
        return topicService.getForumTopics(forumId, lang, lessonId, pageable);
    }
}
