package uz.consortgroup.forum_service.service.service;

import jakarta.persistence.EntityNotFoundException;
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
import uz.consortgroup.forum_service.entity.ForumTopic;
import uz.consortgroup.forum_service.mapper.ForumTopicMapper;
import uz.consortgroup.forum_service.repository.ForumTopicRepository;
import uz.consortgroup.forum_service.security.JwtUserDetails;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ForumTopicServiceImplTest {

    @Mock
    private ForumTopicRepository forumTopicRepository;

    @Mock
    private ForumTopicMapper forumTopicMapper;

    @Mock
    private ForumAccessChecker forumAccessChecker;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ForumTopicServiceImpl forumTopicService;

    @Test
    void createForumTopic_Success() {
        UUID userId = UUID.randomUUID();
        UUID forumId = UUID.randomUUID();
        String title = "Test Title";
        String content = "Test Content";

        CreateForumTopicRequest request = new CreateForumTopicRequest(forumId, title, content);
        ForumTopic forumTopic = ForumTopic.builder()
                .forumId(forumId)
                .authorId(userId)
                .title(title)
                .content(content)
                .build();
        ForumTopicResponse expectedResponse = new ForumTopicResponse();

        mockSecurityContext(userId);
        when(forumTopicRepository.save(any(ForumTopic.class))).thenReturn(forumTopic);
        when(forumTopicMapper.toDto(forumTopic)).thenReturn(expectedResponse);

        ForumTopicResponse result = forumTopicService.createForumTopic(request);

        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(forumAccessChecker).checkAccessOrThrow(userId, forumId);
        verify(forumTopicRepository).save(any(ForumTopic.class));
    }

    @Test
    void createForumTopic_AccessDenied() {
        UUID userId = UUID.randomUUID();
        UUID forumId = UUID.randomUUID();
        CreateForumTopicRequest request = new CreateForumTopicRequest(forumId, "Title", "Content");

        mockSecurityContext(userId);
        doThrow(new SecurityException("Access denied")).when(forumAccessChecker).checkAccessOrThrow(userId, forumId);

        assertThrows(SecurityException.class, () -> forumTopicService.createForumTopic(request));
    }


    @Test
    void findForumTopicById_Success() {
        UUID topicId = UUID.randomUUID();
        ForumTopic forumTopic = new ForumTopic();
        
        when(forumTopicRepository.findById(topicId)).thenReturn(Optional.of(forumTopic));
        
        ForumTopic result = forumTopicService.findForumTopicById(topicId);
        
        assertEquals(forumTopic, result);
    }

    @Test
    void findForumTopicById_NotFound() {
        UUID topicId = UUID.randomUUID();
        
        when(forumTopicRepository.findById(topicId)).thenReturn(Optional.empty());
        
        assertThrows(EntityNotFoundException.class, () -> forumTopicService.findForumTopicById(topicId));
    }

    private void mockSecurityContext(UUID userId) {
        JwtUserDetails userDetails = mock(JwtUserDetails.class);
        when(userDetails.getId()).thenReturn(userId);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);
    }
}