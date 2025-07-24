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
        log.debug("Checking access for userId={} to groupId={}", userId, groupId);

        UUID courseId = accessClient.getCourseIdByGroupId(groupId);
        if (courseId == null) {
            log.warn("Access denied: no course found for groupId={}", groupId);
            throw new AccessDeniedException("Course not found for group");
        }

        ForumAccessRequest request = ForumAccessRequest.builder()
                .userId(userId)
                .courseId(courseId)
                .build();

        ForumAccessResponse response = accessClient.checkAccess(request);
        if (!response.isHasAccess()) {
            log.warn("Access denied: userId={} has no access to courseId={}", userId, courseId);
            throw new AccessDeniedException("User does not have access to this forum");
        }

        log.debug("Access granted: userId={} has access to courseId={}", userId, courseId);
    }
}
