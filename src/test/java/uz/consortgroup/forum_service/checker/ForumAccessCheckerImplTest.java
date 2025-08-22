package uz.consortgroup.forum_service.checker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.consortgroup.core.api.v1.dto.forum.ForumAccessByGroupRequest;
import uz.consortgroup.core.api.v1.dto.forum.ForumAccessResponse;
import uz.consortgroup.core.api.v1.dto.forum.enumeration.ForumAccessReason;
import uz.consortgroup.forum_service.client.UserClient;
import uz.consortgroup.forum_service.exception.AccessDeniedException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ForumAccessCheckerImplTest {

    @Mock
    private UserClient accessClient;

    @InjectMocks
    private ForumAccessCheckerImpl forumAccessChecker;

    private final UUID testUserId = UUID.randomUUID();
    private final UUID testGroupId = UUID.randomUUID();

    @Test
    void shouldAllowAccessWhenUserHasPermission() {
        ForumAccessResponse response = new ForumAccessResponse();
        response.setHasAccess(true);

        when(accessClient.checkAccessByGroup(any(ForumAccessByGroupRequest.class)))
                .thenReturn(response);

        assertDoesNotThrow(() -> forumAccessChecker.checkAccessOrThrow(testUserId, testGroupId));

        verify(accessClient).checkAccessByGroup(argThat(request ->
                request.getUserId().equals(testUserId) &&
                        request.getGroupId().equals(testGroupId)
        ));
    }

    @Test
    void shouldDenyAccessWhenUserHasNoPermission() {
        ForumAccessResponse response = new ForumAccessResponse();
        response.setHasAccess(false);
        response.setReason(ForumAccessReason.FORUM_GROUP_NOT_FOUND);

        when(accessClient.checkAccessByGroup(any(ForumAccessByGroupRequest.class)))
                .thenReturn(response);

        assertThrows(AccessDeniedException.class,
                () -> forumAccessChecker.checkAccessOrThrow(testUserId, testGroupId));

        verify(accessClient).checkAccessByGroup(any(ForumAccessByGroupRequest.class));
    }

    @Test
    void shouldPropagateExceptionWhenFeignClientFails() {
        when(accessClient.checkAccessByGroup(any(ForumAccessByGroupRequest.class)))
                .thenThrow(new RuntimeException("Service unavailable"));

        assertThrows(RuntimeException.class,
                () -> forumAccessChecker.checkAccessOrThrow(testUserId, testGroupId));

        verify(accessClient).checkAccessByGroup(any(ForumAccessByGroupRequest.class));
    }
}
