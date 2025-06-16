package uz.consortgroup.forum_service.checker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import uz.consortgroup.core.api.v1.dto.forum.ForumAccessRequest;
import uz.consortgroup.core.api.v1.dto.forum.ForumAccessResponse;
import uz.consortgroup.forum_service.asspect.annotation.AspectAfterThrowing;
import uz.consortgroup.forum_service.asspect.annotation.LoggingAspectAfterMethod;
import uz.consortgroup.forum_service.asspect.annotation.LoggingAspectBeforeMethod;
import uz.consortgroup.forum_service.client.ForumAccessFeignClient;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ForumAccessCheckerImpl implements ForumAccessChecker {
    private final ForumAccessFeignClient accessClient;

    @Override
    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    @AspectAfterThrowing
    public void checkAccessOrThrow(UUID userId, UUID groupId) {
        UUID courseId = accessClient.getCourseIdByGroupId(groupId);

        if (courseId == null) {
            throw new AccessDeniedException("Course not found for group");
        }

        ForumAccessRequest request = ForumAccessRequest.builder()
                .userId(userId)
                .courseId(courseId)
                .build();

        ForumAccessResponse response = accessClient.checkAccess(request);

        if (!response.isHasAccess()) {
            throw new AccessDeniedException("User does not have access to this forum");
        }
    }
}
