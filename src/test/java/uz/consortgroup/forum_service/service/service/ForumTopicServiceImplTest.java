package uz.consortgroup.forum_service.service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import uz.consortgroup.core.api.v1.dto.forum.CreateForumTopicRequest;
import uz.consortgroup.core.api.v1.dto.forum.ForumTopicResponse;
import uz.consortgroup.forum_service.checker.ForumAccessChecker;
import uz.consortgroup.forum_service.entity.Forum;
import uz.consortgroup.forum_service.entity.ForumTopic;
import uz.consortgroup.forum_service.exception.AccessDeniedException;
import uz.consortgroup.forum_service.mapper.ForumTopicMapper;
import uz.consortgroup.forum_service.repository.ForumTopicRepository;
import uz.consortgroup.forum_service.security.AuthenticatedUser;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ForumTopicServiceImplTest {

    @Mock
    private ForumTopicRepository forumTopicRepository;

    @Mock
    private ForumTopicMapper forumTopicMapper;

    @Mock
    private ForumService forumService;

    @Mock
    private ForumAccessChecker forumAccessChecker;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ForumTopicServiceImpl forumTopicService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createForumTopic_Success() {
        UUID userId = UUID.randomUUID();
        UUID forumId = UUID.randomUUID();
        UUID groupId = UUID.randomUUID();

        String title = "Test Title";
        String content = "Test Content";

        AuthenticatedUser principal = mock(AuthenticatedUser.class);
        when(principal.getUserId()).thenReturn(userId);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(principal);
        SecurityContextHolder.setContext(securityContext);

        Forum forum = mock(Forum.class);
        when(forum.getGroupId()).thenReturn(groupId);
        when(forumService.findForumById(forumId)).thenReturn(forum);

        CreateForumTopicRequest request = new CreateForumTopicRequest();
        request.setTitle(title);
        request.setContent(content);

        when(forumTopicRepository.save(argThat((ForumTopic t) ->
                t.getForum() == forum &&
                        userId.equals(t.getAuthorId()) &&
                        title.equals(t.getTitle()) &&
                        content.equals(t.getContent())
        ))).thenAnswer(inv -> inv.getArgument(0));

        ForumTopicResponse expected = new ForumTopicResponse();
        when(forumTopicMapper.toDto(any(ForumTopic.class))).thenReturn(expected);

        ForumTopicResponse result = forumTopicService.createForumTopic(forumId, request);

        assertNotNull(result);
        assertEquals(expected, result);
        verify(forumAccessChecker).checkAccessOrThrow(userId, groupId);
        verify(forumTopicRepository).save(argThat((ForumTopic t) ->
                t.getForum() == forum &&
                        userId.equals(t.getAuthorId()) &&
                        title.equals(t.getTitle()) &&
                        content.equals(t.getContent())
        ));
        verify(forumTopicMapper).toDto(any(ForumTopic.class));
    }

    @Test
    void createForumTopic_AccessDenied() {
        UUID userId = UUID.randomUUID();
        UUID forumId = UUID.randomUUID();
        UUID groupId = UUID.randomUUID();

        AuthenticatedUser principal = mock(AuthenticatedUser.class);
        when(principal.getUserId()).thenReturn(userId);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(principal);
        SecurityContextHolder.setContext(securityContext);

        Forum forum = mock(Forum.class);
        when(forum.getGroupId()).thenReturn(groupId);
        when(forumService.findForumById(forumId)).thenReturn(forum);

        CreateForumTopicRequest request = new CreateForumTopicRequest();
        request.setTitle("X");
        request.setContent("Y");

        doThrow(new AccessDeniedException("Access denied"))
                .when(forumAccessChecker).checkAccessOrThrow(userId, groupId);

        assertThrows(AccessDeniedException.class, () -> forumTopicService.createForumTopic(forumId, request));
        verify(forumTopicRepository, never()).save(any());
        verify(forumTopicMapper, never()).toDto(any());
    }

    @Test
    void findForumTopicById_Success() {
        UUID topicId = UUID.randomUUID();
        ForumTopic topic = new ForumTopic();

        when(forumTopicRepository.findById(topicId)).thenReturn(Optional.of(topic));

        ForumTopic result = forumTopicService.findForumTopicById(topicId);

        assertSame(topic, result);
        verify(forumTopicRepository).findById(topicId);
    }

    @Test
    void findForumTopicById_NotFound() {
        UUID topicId = UUID.randomUUID();
        when(forumTopicRepository.findById(topicId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> forumTopicService.findForumTopicById(topicId));
        verify(forumTopicRepository).findById(topicId);
    }
}
