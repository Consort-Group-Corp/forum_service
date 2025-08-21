package uz.consortgroup.forum_service.checker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uz.consortgroup.core.api.v1.dto.forum.ForumAccessByGroupRequest;
import uz.consortgroup.core.api.v1.dto.forum.ForumAccessResponse;
import uz.consortgroup.forum_service.client.UserClient;
import uz.consortgroup.forum_service.exception.AccessDeniedException;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ForumAccessCheckerImpl implements ForumAccessChecker {
    private final UserClient accessClient;

    @Override
    public void checkAccessOrThrow(UUID userId, UUID groupId) {
        log.debug("Checking access for userId={} to groupId={}", userId, groupId);

        ForumAccessByGroupRequest request = ForumAccessByGroupRequest.builder()
                .userId(userId)
                .groupId(groupId)
                .build();

        ForumAccessResponse response = accessClient.checkAccessByGroup(request);
        if (!response.isHasAccess()) {
            log.warn("Access denied: userId={} groupId={} reason={}", userId, groupId, response.getReason());
            throw new AccessDeniedException(String.format("Access denied: userId=%s groupId=%s reason=%s", userId, groupId, response.getReason()));
        }

        log.debug("Access granted: userId={} groupId={}", userId, groupId);
    }
}
