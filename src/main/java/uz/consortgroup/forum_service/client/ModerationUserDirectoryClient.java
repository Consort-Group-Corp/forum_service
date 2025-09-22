package uz.consortgroup.forum_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import uz.consortgroup.core.api.v1.dto.forum.moderation.response.ModerationUserInfoResponseDto;
import uz.consortgroup.forum_service.config.FeignClientConfig;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(
        name = "user-service",
        contextId = "moderationClient",
        url = "${user.service.url}",
        configuration = FeignClientConfig.class)
public interface ModerationUserDirectoryClient {

    @PostMapping("/moderation-users")
    Map<UUID, ModerationUserInfoResponseDto> getModerationUsers(@RequestBody List<UUID> ids);
}
