package uz.consortgroup.forum_service.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uz.consortgroup.forum_service.exception.ForumNotFoundException;
import uz.consortgroup.forum_service.service.service.ForumCommentService;
import uz.consortgroup.forum_service.util.JwtUtils;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = ForumStatisticsController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class,
                OAuth2ResourceServerAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
class ForumStatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ForumCommentService forumCommentService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Test
    void totalComments_ok() throws Exception {
        when(forumCommentService.getTotalCommentsCount()).thenReturn(12000L);

        mockMvc.perform(get("/api/v1/forums/comments/total").accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.totalCount", is(12000)));
    }

    @Test
    void commentsByForum_ok() throws Exception {
        UUID forumId = UUID.randomUUID();
        when(forumCommentService.getCommentsCountByForumId(forumId)).thenReturn(345L);

        mockMvc.perform(get("/api/v1/forums/{forumId}/comments/total", forumId).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.totalCount", is(345)));
    }

    @Test
    void commentsByForum_notFound_forumException() throws Exception {
        UUID forumId = UUID.randomUUID();
        when(forumCommentService.getCommentsCountByForumId(forumId))
                .thenThrow(new ForumNotFoundException("Forum not found"));

        mockMvc.perform(get("/api/v1/forums/{forumId}/comments/total", forumId).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void commentsByForum_invalidUuid() throws Exception {
        mockMvc.perform(get("/api/v1/forums/{forumId}/comments/total", "not-a-uuid").accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
