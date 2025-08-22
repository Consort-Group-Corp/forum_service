package uz.consortgroup.forum_service.service.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import uz.consortgroup.core.api.v1.dto.course.request.course.CourseIdsRequest;
import uz.consortgroup.core.api.v1.dto.forum.ForumAuthorDto;
import uz.consortgroup.core.api.v1.dto.forum.ForumListItemResponseDto;
import uz.consortgroup.core.api.v1.dto.forum.ForumListParamsRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.ForumListResponseDto;
import uz.consortgroup.core.api.v1.dto.forum.enumeration.AccessFilter;
import uz.consortgroup.core.api.v1.dto.forum.enumeration.CreatedAtSort;
import uz.consortgroup.core.api.v1.dto.forum.enumeration.SortDir;
import uz.consortgroup.core.api.v1.dto.user.enumeration.UserRole;
import uz.consortgroup.forum_service.client.CourseClient;
import uz.consortgroup.forum_service.client.UserClient;
import uz.consortgroup.forum_service.entity.Forum;
import uz.consortgroup.forum_service.mapper.ForumListMapper;
import uz.consortgroup.forum_service.repository.ForumRepository;
import uz.consortgroup.forum_service.security.AuthenticatedUser;
import uz.consortgroup.forum_service.service.strategy.ForumVisibilityStrategy;
import uz.consortgroup.forum_service.service.strategy.ForumVisibilityStrategyFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ForumListQueryServiceImplTest {

    @Mock
    private ForumRepository forumRepository;

    @Mock
    private ForumVisibilityStrategyFactory visibilityStrategyFactory;

    @Mock
    private ForumCommentService forumCommentService;

    @Mock
    private ForumLikeService forumLikeService;

    @Mock
    private CourseClient courseClient;

    @Mock
    private UserClient userClient;

    @Mock
    private ForumListMapper forumListMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ForumListQueryServiceImpl service;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private UUID mockPrincipal(UserRole role) {
        UUID userId = UUID.randomUUID();
        AuthenticatedUser principal = mock(AuthenticatedUser.class);
        when(principal.getUserId()).thenReturn(userId);
        when(principal.getRole()).thenReturn(role);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(principal);
        SecurityContextHolder.setContext(securityContext);
        return userId;
    }

    @SuppressWarnings("unchecked")
    private void mockVisibilityStrategy(UserRole role) {
        ForumVisibilityStrategy strategy = mock(ForumVisibilityStrategy.class);
        when(visibilityStrategyFactory.forRole(role)).thenReturn(strategy);
        when(strategy.spec(any(ForumListParamsRequestDto.class), any(UUID.class)))
                .thenReturn((Specification<Forum>) (root, query, cb) -> null);
    }

    @Test
    void list_HappyPath_ReturnsData() {
        UUID actorId = mockPrincipal(UserRole.MENTOR);
        mockVisibilityStrategy(UserRole.MENTOR);

        UUID f1 = UUID.randomUUID(), f2 = UUID.randomUUID();
        UUID c1 = UUID.randomUUID(), c2 = UUID.randomUUID();
        UUID o1 = UUID.randomUUID(), o2 = UUID.randomUUID();

        Forum forum1 = mock(Forum.class);
        when(forum1.getId()).thenReturn(f1);
        when(forum1.getCourseId()).thenReturn(c1);
        when(forum1.getOwnerId()).thenReturn(o1);

        Forum forum2 = mock(Forum.class);
        when(forum2.getId()).thenReturn(f2);
        when(forum2.getCourseId()).thenReturn(c2);
        when(forum2.getOwnerId()).thenReturn(o2);

        List<Forum> content = List.of(forum1, forum2);
        Page<Forum> page = new PageImpl<>(content, PageRequest.of(1, 10), 42);
        when(forumRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        when(forumCommentService.countByForumIds(List.of(f1, f2))).thenReturn(Map.of(f1, 3L, f2, 0L));
        when(forumLikeService.countByForumIds(List.of(f1, f2))).thenReturn(Map.of(f1, 1L, f2, 5L));

        when(courseClient.getCourseCovers(any(CourseIdsRequest.class))).thenReturn(List.of());
        when(userClient.getAuthors(any())).thenReturn(Map.of());

        when(forumListMapper.toListItem(any(Forum.class), anyLong(), anyLong(), any(), any()))
                .thenAnswer(inv -> {
                    Forum f = inv.getArgument(0);
                    long comments = inv.getArgument(1);
                    long likes = inv.getArgument(2);
                    String cover = inv.getArgument(3);
                    ForumAuthorDto author = inv.getArgument(4);
                    return ForumListItemResponseDto.builder()
                            .id(f.getId())
                            .title("T-" + f.getId())
                            .commentsCount(comments)
                            .likesCount(likes)
                            .previewImageUrl(cover)
                            .author(author)
                            .build();
                });


        ForumListParamsRequestDto params = ForumListParamsRequestDto.builder()
                .search("java")
                .accessFilter(AccessFilter.OPEN)
                .commentsCount(SortDir.DESC)
                .createdAt(CreatedAtSort.NEWEST)
                .page(2)
                .limit(10)
                .build();

        ForumListResponseDto resp = service.list(params);

        assertNotNull(resp);
        assertEquals(42L, resp.getTotal());
        assertEquals(2, resp.getPage());
        assertEquals(10, resp.getLimit());
        assertEquals(2, resp.getData().size());

        ForumListItemResponseDto i1 = resp.getData().get(0);
        ForumListItemResponseDto i2 = resp.getData().get(1);

        assertEquals(f1, i1.getId());
        assertEquals(3L, i1.getCommentsCount());
        assertEquals(1L, i1.getLikesCount());
        assertEquals(f2, i2.getId());
        assertEquals(0L, i2.getCommentsCount());
        assertEquals(5L, i2.getLikesCount());

        verify(forumRepository).findAll(any(Specification.class), any(Pageable.class));
        verify(forumCommentService).countByForumIds(List.of(f1, f2));
        verify(forumLikeService).countByForumIds(List.of(f1, f2));
        verify(courseClient).getCourseCovers(any(CourseIdsRequest.class));
        verify(userClient).getAuthors(any());
        verify(forumListMapper, times(2)).toListItem(any(), anyLong(), anyLong(), any(), any());
    }

    @Test
    void list_EmptyPage_ReturnsEmptyDataAndCallsDependenciesWithEmptyLists() {
        mockPrincipal(UserRole.STUDENT);
        mockVisibilityStrategy(UserRole.STUDENT);

        Page<Forum> empty = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
        when(forumRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(empty);

        when(courseClient.getCourseCovers(any(CourseIdsRequest.class))).thenReturn(List.of());
        when(userClient.getAuthors(any())).thenReturn(Map.of());
        when(forumCommentService.countByForumIds(List.of())).thenReturn(Map.of());
        when(forumLikeService.countByForumIds(List.of())).thenReturn(Map.of());

        ForumListParamsRequestDto params = ForumListParamsRequestDto.builder()
                .accessFilter(AccessFilter.ALL)
                .page(null)
                .limit(null)
                .build();

        ForumListResponseDto resp = service.list(params);

        assertNotNull(resp);
        assertEquals(0L, resp.getTotal());
        assertEquals(1, resp.getPage());
        assertEquals(20, resp.getLimit());
        assertTrue(resp.getData().isEmpty());

        verify(forumCommentService).countByForumIds(eq(List.of()));
        verify(forumLikeService).countByForumIds(eq(List.of()));
        verify(courseClient).getCourseCovers(any(CourseIdsRequest.class));
        verify(userClient).getAuthors(any());
        verifyNoMoreInteractions(forumListMapper);
    }

    @Test
    void list_ClientThrows_PropagatesException() {
        mockPrincipal(UserRole.MENTOR);
        mockVisibilityStrategy(UserRole.MENTOR);

        ForumListParamsRequestDto params = ForumListParamsRequestDto.builder()
                .page(1)
                .limit(20)
                .build();

        assertThrows(RuntimeException.class, () -> service.list(params));
    }

    @Test
    void list_OldestSort_BuildsAscendingCreatedAtAndCorrectPageable() {
        mockPrincipal(UserRole.MENTOR);
        mockVisibilityStrategy(UserRole.MENTOR);

        when(forumRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 15), 0));

        when(forumCommentService.countByForumIds(anyList())).thenReturn(Map.of());
        when(forumLikeService.countByForumIds(anyList())).thenReturn(Map.of());
        when(courseClient.getCourseCovers(any(CourseIdsRequest.class))).thenReturn(List.of());
        when(userClient.getAuthors(any())).thenReturn(Map.of());

        ForumListParamsRequestDto params = ForumListParamsRequestDto.builder()
                .accessFilter(AccessFilter.ALL)
                .createdAt(CreatedAtSort.OLDEST)
                .page(1)
                .limit(15)
                .build();

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        ForumListResponseDto resp = service.list(params);

        assertNotNull(resp);
        verify(forumRepository).findAll(any(Specification.class), pageableCaptor.capture());

        Pageable p = pageableCaptor.getValue();
        assertEquals(0, p.getPageNumber());
        assertEquals(15, p.getPageSize());

        assertTrue(p.getSort().iterator().hasNext(), "Sort should be present");
        var order = p.getSort().iterator().next();
        assertEquals("createdAt", order.getProperty());
        assertTrue(order.isAscending(), "createdAt must be ascending for OLDEST");
    }
}
