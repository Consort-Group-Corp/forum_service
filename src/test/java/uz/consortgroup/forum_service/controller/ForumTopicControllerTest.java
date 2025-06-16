package uz.consortgroup.forum_service.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uz.consortgroup.core.api.v1.dto.forum.CreateForumTopicRequest;
import uz.consortgroup.core.api.v1.dto.forum.ForumTopicResponse;
import uz.consortgroup.forum_service.service.service.ForumTopicService;
import uz.consortgroup.forum_service.util.JwtAuthFilter;
import uz.consortgroup.forum_service.util.JwtUtils;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ForumTopicController.class)
@AutoConfigureMockMvc(addFilters = false)
class ForumTopicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ForumTopicService forumTopicService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    @WithMockUser(roles = "ADMIN")
    void createForumTopic_ValidRequest_ReturnsCreated() throws Exception {
        UUID testId = UUID.randomUUID();
        ForumTopicResponse response = ForumTopicResponse.builder()
                .id(testId)
                .title("Test Topic")
                .content("Test Content")
                .authorId(UUID.randomUUID())
                .createdAt(Instant.now())
                .build();

        when(forumTopicService.createForumTopic(any(CreateForumTopicRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/forum/forum-topic")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"forumId\":\"" + UUID.randomUUID() + "\",\"title\":\"Test Topic\",\"content\":\"Test Content\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.title").value("Test Topic"))
                .andExpect(jsonPath("$.content").value("Test Content"));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void createForumTopic_MissingForumId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/forum/forum-topic")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Test Topic\",\"content\":\"Test Content\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createForumTopic_EmptyTitle_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/forum/forum-topic")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"forumId\":\"" + UUID.randomUUID() + "\",\"title\":\"\",\"content\":\"Test Content\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createForumTopic_EmptyContent_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/forum/forum-topic")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"forumId\":\"" + UUID.randomUUID() + "\",\"title\":\"Test Topic\",\"content\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createForumTopic_ServiceError_ReturnsInternalServerError() throws Exception {
        when(forumTopicService.createForumTopic(any(CreateForumTopicRequest.class)))
                .thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(post("/api/v1/forum/forum-topic")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"forumId\":\"" + UUID.randomUUID() + "\",\"title\":\"Test Topic\",\"content\":\"Test Content\"}"))
                .andExpect(status().isInternalServerError());
    }
}