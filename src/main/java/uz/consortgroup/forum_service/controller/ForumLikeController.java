package uz.consortgroup.forum_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uz.consortgroup.core.api.v1.dto.forum.response.ForumLikeResponseDto;
import uz.consortgroup.forum_service.service.ForumLikeService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/forum/likes")
@RequiredArgsConstructor
public class ForumLikeController {

    private final ForumLikeService likeService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{forumId}")
    public ForumLikeResponseDto addLikeToForum(@PathVariable UUID forumId) {
        return likeService.addLikeToForum(forumId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{forumId}/unlike")
    public ForumLikeResponseDto removeLikeFromForum(@PathVariable UUID forumId) {
        return likeService.removeLikeFromForum(forumId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{forumId}/status")
    public ForumLikeResponseDto getForumLikesStatus(@PathVariable UUID forumId) {
        return likeService.getForumLikeStatus(forumId);
    }
}
