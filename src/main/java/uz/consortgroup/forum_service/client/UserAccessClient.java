package uz.consortgroup.forum_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uz.consortgroup.core.api.v1.dto.user.response.CourseAccessResponse;
import uz.consortgroup.forum_service.config.FeignClientConfig;

import java.util.UUID;

@FeignClient(
        name = "user-service",
        contextId = "userClient",
        url = "${user.service.url}",
        configuration = FeignClientConfig.class
)
public interface UserAccessClient {

    @GetMapping("/api/v1/internal/access/{userId}/courses/{courseId}")
    CourseAccessResponse checkAccess(@PathVariable("userId") UUID userId,
                                     @PathVariable("courseId") UUID courseId);
}
