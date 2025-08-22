package uz.consortgroup.forum_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import uz.consortgroup.core.api.v1.dto.forum.ForumAccessByCourseRequest;
import uz.consortgroup.core.api.v1.dto.forum.ForumAccessByGroupRequest;
import uz.consortgroup.core.api.v1.dto.forum.ForumAccessResponse;
import uz.consortgroup.core.api.v1.dto.forum.ForumAuthorDto;
import uz.consortgroup.core.api.v1.dto.forum.ForumAuthorIdsRequest;
import uz.consortgroup.forum_service.config.FeignClientConfig;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(
        name = "user-service",
        contextId = "userClient",
        url = "${user.service.url}",
        configuration = FeignClientConfig.class
)
public interface UserClient {

    @PostMapping("/api/v1/forum-access/by-course")
    ForumAccessResponse checkAccessByCourse(@RequestBody ForumAccessByCourseRequest request);

    @PostMapping("/api/v1/forum-access/by-group")
    ForumAccessResponse checkAccessByGroup(@RequestBody ForumAccessByGroupRequest request);

    @GetMapping("/api/v1/forum-access/course-id/{groupId}")
    UUID getCourseIdByGroupId(@PathVariable("groupId") UUID forumId);

    @PostMapping("/internal/forum-directory/authors/batch")
    Map<UUID, ForumAuthorDto> getAuthors(@RequestBody ForumAuthorIdsRequest request);

    @GetMapping("/internal/forum-directory/users/{userId}/groups")
    List<UUID> getGroupIdsForUser(@PathVariable("userId") UUID userId);
}
