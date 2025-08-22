package uz.consortgroup.forum_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uz.consortgroup.core.api.v1.dto.forum.*;
import uz.consortgroup.core.api.v1.dto.user.enumeration.UserRole;
import uz.consortgroup.forum_service.service.service.ForumListQueryService;
import uz.consortgroup.forum_service.util.AuthTokenFilter;
import uz.consortgroup.forum_service.util.JwtUtils;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ForumQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
class ForumQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ForumListQueryService forumListQueryService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private AuthTokenFilter authTokenFilter;

    private final UUID authorId = UUID.randomUUID();
    private final UUID forumId = UUID.randomUUID();

    @Test
    @WithMockUser
    void list_ValidRequest_ReturnsOk() throws Exception {
        ForumAuthorDto author = ForumAuthorDto.builder()
                .id(authorId)
                .firstName("Doniyor")
                .lastName("Kurbanov")
                .middleName("Ruslanovich")
                .role(UserRole.MENTOR)
                .build();

        ForumListItemResponseDto item = ForumListItemResponseDto.builder()
                .id(forumId)
                .title("Курс по Java")
                .author(author)
                .createdAt(Instant.now())
                .commentsCount(5L)
                .likesCount(10L)
                .accessType(uz.consortgroup.core.api.v1.dto.user.enumeration.ForumAccessType.OPEN)
                .previewImageUrl("http://image.png")
                .build();

        ForumListResponseDto response = ForumListResponseDto.builder()
                .total(1L)
                .page(1)
                .limit(10)
                .data(List.of(item))
                .build();

        when(forumListQueryService.list(any(ForumListParamsRequestDto.class))).thenReturn(response);

        mockMvc.perform(get("/api/v1/forums")
                        .param("search", "java")
                        .param("accessFilter", "OPEN")
                        .param("mentorId", authorId.toString())
                        .param("commentsCount", "DESC")
                        .param("likesCount", "ASC")
                        .param("createdAt", "NEWEST")
                        .param("page", "1")
                        .param("limit", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.limit").value(10))
                .andExpect(jsonPath("$.data[0].id").value(forumId.toString()))
                .andExpect(jsonPath("$.data[0].title").value("Курс по Java"))
                .andExpect(jsonPath("$.data[0].author.firstName").value("Doniyor"))
                .andExpect(jsonPath("$.data[0].commentsCount").value(5))
                .andExpect(jsonPath("$.data[0].likesCount").value(10));
    }

    @Test
    @WithMockUser
    void list_InvalidPage_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/forums")
                        .param("page", "0") // невалидно, минимум 1
                        .param("limit", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void list_InvalidLimitTooSmall_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/forums")
                        .param("page", "1")
                        .param("limit", "0")) // невалидно, минимум 1
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void list_InvalidLimitTooBig_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/forums")
                        .param("page", "1")
                        .param("limit", "200")) // невалидно, максимум 100
                .andExpect(status().isBadRequest());
    }
}
