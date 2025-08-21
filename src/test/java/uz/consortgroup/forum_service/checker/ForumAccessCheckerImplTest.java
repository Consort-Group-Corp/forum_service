package uz.consortgroup.forum_service.checker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import uz.consortgroup.core.api.v1.dto.forum.ForumAccessByCourseRequest;
import uz.consortgroup.core.api.v1.dto.forum.ForumAccessResponse;
import uz.consortgroup.forum_service.client.UserClient;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.never;
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
    private final UUID testCourseId = UUID.randomUUID();

    @Test
    void shouldAllowAccessWhenUserHasPermission() {
        when(accessClient.getCourseIdByGroupId(testGroupId)).thenReturn(testCourseId);
        ForumAccessResponse response = new ForumAccessResponse();
        response.setHasAccess(true);
        when(accessClient.checkAccessByCourse(any(ForumAccessByCourseRequest.class)))
                .thenReturn(response);

        assertDoesNotThrow(() -> forumAccessChecker.checkAccessOrThrow(testUserId, testGroupId));

        verify(accessClient).getCourseIdByGroupId(testGroupId);
        verify(accessClient).checkAccessByCourse(argThat(request ->
                request.getUserId().equals(testUserId) &&
                        request.getCourseId().equals(testCourseId)));
    }

    @Test
    void shouldDenyAccessWhenUserHasNoPermission() {
        when(accessClient.getCourseIdByGroupId(testGroupId)).thenReturn(testCourseId);
        when(accessClient.checkAccessByCourse(any(ForumAccessByCourseRequest.class)))
                .thenReturn(new ForumAccessResponse());

        assertThrows(AccessDeniedException.class,
                () -> forumAccessChecker.checkAccessOrThrow(testUserId, testGroupId));

        verify(accessClient).getCourseIdByGroupId(testGroupId);
        verify(accessClient).checkAccessByCourse(any(ForumAccessByCourseRequest.class));
    }

    @Test
    void shouldThrowExceptionWhenCourseNotFound() {
        when(accessClient.getCourseIdByGroupId(testGroupId)).thenReturn(null);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> forumAccessChecker.checkAccessOrThrow(testUserId, testGroupId));

        assertEquals("Course not found for group", exception.getMessage());
        verify(accessClient).getCourseIdByGroupId(testGroupId);
        verify(accessClient, never()).checkAccessByCourse(any());
    }

    @Test
    void shouldThrowExceptionWhenFeignClientFails() {
        when(accessClient.getCourseIdByGroupId(testGroupId)).thenReturn(testCourseId);
        when(accessClient.checkAccessByCourse(any(ForumAccessByCourseRequest.class)))
                .thenThrow(new RuntimeException("Service unavailable"));

        assertThrows(RuntimeException.class,
                () -> forumAccessChecker.checkAccessOrThrow(testUserId, testGroupId));

        verify(accessClient).getCourseIdByGroupId(testGroupId);
        verify(accessClient).checkAccessByCourse(any(ForumAccessByCourseRequest.class));
    }
}