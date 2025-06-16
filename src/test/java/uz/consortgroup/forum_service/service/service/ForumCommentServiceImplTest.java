package uz.consortgroup.forum_service.service.service;

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
import uz.consortgroup.forum_service.entity.ForumTopic;
import uz.consortgroup.forum_service.mapper.ForumCommentMapper;
import uz.consortgroup.forum_service.repository.ForumCommentRepository;
import uz.consortgroup.forum_service.security.JwtUserDetails;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    void createComment_Success() {
        UUID userId = UUID.randomUUID();
        UUID topicId = UUID.randomUUID();
        UUID forumId = UUID.randomUUID();
        String content = "Test content";

        CreateForumCommentRequest request = new CreateForumCommentRequest(topicId, content);
        ForumTopic topic = new ForumTopic();
        topic.setForumId(forumId);

        ForumCommentResponse expectedResponse = new ForumCommentResponse();

        JwtUserDetails userDetails = mock(JwtUserDetails.class);
        when(userDetails.getId()).thenReturn(userId);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);

        when(forumTopicService.findForumTopicById(topicId)).thenReturn(topic);
        when(forumCommentRepository.save(argThat(comment ->
                comment.getForumTopic().equals(topic) &&
                        comment.getAuthorId().equals(userId) &&
                        comment.getContent().equals(content))))
                .thenAnswer(inv -> inv.getArgument(0));

        when(forumCommentMapper.toDto(argThat(comment ->
                comment.getForumTopic().equals(topic) &&
                        comment.getAuthorId().equals(userId) &&
                        comment.getContent().equals(content))))
                .thenReturn(expectedResponse);

        ForumCommentResponse result = forumCommentService.createComment(request);

        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(forumAccessChecker).checkAccessOrThrow(userId, forumId);
        verify(forumCommentRepository).save(argThat(comment ->
                comment.getForumTopic().equals(topic) &&
                        comment.getAuthorId().equals(userId) &&
                        comment.getContent().equals(content)));
    }

    @Test
    void createComment_TopicNotFound() {
        UUID topicId = UUID.randomUUID();
        CreateForumCommentRequest request = new CreateForumCommentRequest(topicId, "content");

        when(forumTopicService.findForumTopicById(topicId)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> forumCommentService.createComment(request));
    }

    @Test
    void createComment_AccessDenied() {
        UUID userId = UUID.randomUUID();
        UUID topicId = UUID.randomUUID();
        UUID forumId = UUID.randomUUID();
        CreateForumCommentRequest request = new CreateForumCommentRequest(topicId, "content");
        ForumTopic topic = new ForumTopic();
        topic.setForumId(forumId);

        JwtUserDetails userDetails = mock(JwtUserDetails.class);
        when(userDetails.getId()).thenReturn(userId);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);

        when(forumTopicService.findForumTopicById(topicId)).thenReturn(topic);
        doThrow(new SecurityException("Access denied")).when(forumAccessChecker).checkAccessOrThrow(userId, forumId);

        assertThrows(SecurityException.class, () -> forumCommentService.createComment(request));
    }
}