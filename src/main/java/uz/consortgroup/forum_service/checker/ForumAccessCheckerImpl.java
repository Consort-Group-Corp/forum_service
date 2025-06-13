package uz.consortgroup.forum_service.checker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import uz.consortgroup.core.api.v1.dto.forum.ForumAccessRequest;
import uz.consortgroup.core.api.v1.dto.forum.ForumAccessResponse;
import uz.consortgroup.forum_service.client.ForumAccessFeignClient;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ForumAccessCheckerImpl implements ForumAccessChecker {
    private final ForumAccessFeignClient accessClient;

    @Override
    public void checkAccessOrThrow(UUID userId, UUID groupId) {
        UUID courseId = accessClient.getCourseIdByGroupId(groupId);
        log.info("Checking access for user {} to forum {}, resolved courseId: {}", userId, groupId, courseId);

        ForumAccessRequest request = ForumAccessRequest.builder()
                .userId(userId)
                .courseId(courseId)
                .build();

        ForumAccessResponse response = accessClient.checkAccess(request);

        if (!response.isHasAccess()) {
            log.warn("Access denied: {}", response.getReason());
            throw new AccessDeniedException("User does not have access to this forum");
        }
    }

}
