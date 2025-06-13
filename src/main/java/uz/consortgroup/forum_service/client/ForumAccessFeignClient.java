package uz.consortgroup.forum_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import uz.consortgroup.core.api.v1.dto.forum.ForumAccessRequest;
import uz.consortgroup.core.api.v1.dto.forum.ForumAccessResponse;
import uz.consortgroup.forum_service.config.FeignClientConfig;

import java.util.UUID;

@FeignClient(
        name = "course-service",
        contextId = "courseClient",
        url = "${user.service.url}",
        configuration = FeignClientConfig.class
)
public interface ForumAccessFeignClient {

    @PostMapping("/api/v1/forum-access/access")
    ForumAccessResponse checkAccess(@RequestBody ForumAccessRequest request);

    @GetMapping("/api/v1/forum-access/course-id/{groupId}")
    UUID getCourseIdByGroupId(@PathVariable("groupId") UUID forumId);
}
