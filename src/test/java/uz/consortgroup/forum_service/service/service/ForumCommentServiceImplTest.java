package uz.consortgroup.forum_service.service.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import uz.consortgroup.core.api.v1.dto.forum.CreateForumCommentRequest;
import uz.consortgroup.core.api.v1.dto.forum.ForumCommentResponse;
import uz.consortgroup.forum_service.checker.ForumAccessChecker;
import uz.consortgroup.forum_service.entity.Forum;
import uz.consortgroup.forum_service.entity.ForumComment;
import uz.consortgroup.forum_service.entity.ForumTopic;
import uz.consortgroup.forum_service.exception.AccessDeniedException;
import uz.consortgroup.forum_service.mapper.ForumCommentMapper;
import uz.consortgroup.forum_service.repository.ForumCommentRepository;
import uz.consortgroup.forum_service.security.AuthenticatedUser;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ForumCommentServiceImplTest {

    @Mock
    private ForumTopicService forumTopicService;

    @Mock
    private ForumCommentRepository forumCommentRepository;

    @Mock
    private ForumCommentMapper forumCommentMapper;

    @Mock
    private ForumAccessChecker forumAccessChecker;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ForumCommentServiceImpl forumCommentService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createComment_Success() {
        UUID userId = UUID.randomUUID();
        UUID topicId = UUID.randomUUID();
        UUID forumId = UUID.randomUUID();
        UUID groupId = UUID.randomUUID();
        String content = "Test content";

        AuthenticatedUser principal = mock(AuthenticatedUser.class);
        when(principal.getUserId()).thenReturn(userId);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(principal);
        SecurityContextHolder.setContext(securityContext);

        ForumTopic topic = mock(ForumTopic.class);
        Forum forum = mock(Forum.class);
        when(topic.getForum()).thenReturn(forum);
        when(forum.getId()).thenReturn(forumId);
        when(forum.getGroupId()).thenReturn(groupId);
        when(forumTopicService.findForumTopicById(topicId)).thenReturn(topic);

        CreateForumCommentRequest request = new CreateForumCommentRequest();
        request.setContent(content);

        when(forumCommentRepository.save(argThat((ForumComment c) ->
                c.getForumTopic() == topic &&
                        userId.equals(c.getAuthorId()) &&
                        content.equals(c.getContent())
        ))).thenAnswer(inv -> inv.getArgument(0));


        ForumCommentResponse expected = new ForumCommentResponse();
        when(forumCommentMapper.toDto(argThat((ForumComment c) ->
                c.getForumTopic() == topic &&
                        userId.equals(c.getAuthorId()) &&
                        content.equals(c.getContent())
        ))).thenReturn(expected);

        ForumCommentResponse result = forumCommentService.createComment(topicId, request);

        assertNotNull(result);
        assertEquals(expected, result);
        verify(forumAccessChecker).checkAccessOrThrow(userId, groupId);
        verify(forumCommentRepository).save(any(ForumComment.class));
        verify(forumCommentMapper).toDto(any(ForumComment.class));
    }

    @Test
    void createComment_TopicNotFound_ThrowsNpe() {
        UUID topicId = UUID.randomUUID();
        CreateForumCommentRequest request = new CreateForumCommentRequest();
        request.setContent("x");

        AuthenticatedUser principal = mock(AuthenticatedUser.class);
        when(principal.getUserId()).thenReturn(UUID.randomUUID());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(principal);
        SecurityContextHolder.setContext(securityContext);

        when(forumTopicService.findForumTopicById(topicId)).thenReturn(null);

        assertThrows(NullPointerException.class, () -> forumCommentService.createComment(topicId, request));
        verify(forumCommentRepository, never()).save(any());
        verify(forumCommentMapper, never()).toDto(any());
        verify(forumAccessChecker, never()).checkAccessOrThrow(any(), any());
    }

    @Test
    void createComment_AccessDenied_PropagatesException() {
        UUID userId = UUID.randomUUID();
        UUID topicId = UUID.randomUUID();
        UUID groupId = UUID.randomUUID();

        AuthenticatedUser principal = mock(AuthenticatedUser.class);
        when(principal.getUserId()).thenReturn(userId);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(principal);
        SecurityContextHolder.setContext(securityContext);

        ForumTopic topic = mock(ForumTopic.class);
        Forum forum = mock(Forum.class);
        when(topic.getForum()).thenReturn(forum);
        when(forum.getGroupId()).thenReturn(groupId);
        when(forumTopicService.findForumTopicById(topicId)).thenReturn(topic);

        CreateForumCommentRequest request = new CreateForumCommentRequest();
        request.setContent("blocked");

        doThrow(new AccessDeniedException("Access denied")).when(forumAccessChecker)
                .checkAccessOrThrow(userId, groupId);

        assertThrows(AccessDeniedException.class, () -> forumCommentService.createComment(topicId, request));
        verify(forumCommentRepository, never()).save(any());
        verify(forumCommentMapper, never()).toDto(any());
    }
}
