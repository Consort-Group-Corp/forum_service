package uz.consortgroup.forum_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uz.consortgroup.core.api.v1.dto.forum.CreateForumCommentRequest;
import uz.consortgroup.core.api.v1.dto.forum.ForumCommentResponse;
import uz.consortgroup.forum_service.service.service.ForumCommentService;
import uz.consortgroup.forum_service.util.JwtAuthFilter;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ForumCommentController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthFilter.class))
class ForumCommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ForumCommentService forumCommentService;

    @Test
    @WithMockUser
    void shouldCreateCommentSuccessfully() throws Exception {
        ForumCommentResponse response = ForumCommentResponse.builder()
                .id(UUID.randomUUID())
                .topicId(UUID.randomUUID())
                .authorId(UUID.randomUUID())
                .content("Test content")
                .createdAt(Instant.now())
                .build();

        when(forumCommentService.createComment(any(CreateForumCommentRequest.class)))
                .thenReturn(response);

        CreateForumCommentRequest request = new CreateForumCommentRequest();
        request.setTopicId(UUID.randomUUID());
        request.setContent("Test content");

        mockMvc.perform(post("/api/v1/forum/forum-comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.content").value("Test content"));
    }

    @Test
    @WithMockUser
    void shouldReturnBadRequestWhenContentIsBlank() throws Exception {
        CreateForumCommentRequest request = new CreateForumCommentRequest();
        request.setTopicId(UUID.randomUUID());
        request.setContent("");

        mockMvc.perform(post("/api/v1/forum/forum-comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void shouldReturnBadRequestWhenTopicIdIsNull() throws Exception {
        CreateForumCommentRequest request = new CreateForumCommentRequest();
        request.setTopicId(null);
        request.setContent("Test content");

        mockMvc.perform(post("/api/v1/forum/forum-comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void shouldHandleServiceException() throws Exception {
        when(forumCommentService.createComment(any(CreateForumCommentRequest.class)))
                .thenThrow(new RuntimeException("Service error"));

        CreateForumCommentRequest request = new CreateForumCommentRequest();
        request.setTopicId(UUID.randomUUID());
        request.setContent("Test content");

        mockMvc.perform(post("/api/v1/forum/forum-comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().is5xxServerError());
    }
}