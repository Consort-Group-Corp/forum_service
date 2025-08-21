package uz.consortgroup.forum_service.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.course.request.course.CourseCoverDto;
import uz.consortgroup.core.api.v1.dto.course.request.course.CourseIdsRequest;
import uz.consortgroup.core.api.v1.dto.forum.ForumAuthorDto;
import uz.consortgroup.core.api.v1.dto.forum.ForumAuthorIdsRequest;
import uz.consortgroup.core.api.v1.dto.forum.ForumListItemResponseDto;
import uz.consortgroup.core.api.v1.dto.forum.ForumListParamsRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.ForumListResponseDto;
import uz.consortgroup.core.api.v1.dto.forum.enumeration.AccessFilter;
import uz.consortgroup.core.api.v1.dto.forum.enumeration.CreatedAtSort;
import uz.consortgroup.core.api.v1.dto.user.enumeration.UserRole;
import uz.consortgroup.forum_service.client.CourseClient;
import uz.consortgroup.forum_service.client.UserClient;
import uz.consortgroup.forum_service.entity.Forum;
import uz.consortgroup.forum_service.mapper.ForumListMapper;
import uz.consortgroup.forum_service.repository.ForumRepository;
import uz.consortgroup.forum_service.repository.spec.ForumSpecs;
import uz.consortgroup.forum_service.security.AuthenticatedUser;
import uz.consortgroup.forum_service.service.strategy.ForumVisibilityStrategy;
import uz.consortgroup.forum_service.service.strategy.ForumVisibilityStrategyFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForumListQueryServiceImpl implements ForumListQueryService {

    private final ForumRepository forumRepository;
    private final ForumVisibilityStrategyFactory visibilityStrategyFactory;
    private final ForumCommentService forumCommentService;
    private final ForumLikeService forumLikeService;
    private final CourseClient courseClient;
    private final UserClient userClient;
    private final ForumListMapper forumListMapper;

    @Override
    @Transactional(readOnly = true)
    public ForumListResponseDto list(ForumListParamsRequestDto params) {
        log.info("Processing forum list request for params: {}", params);

        AuthenticatedUser u = (AuthenticatedUser) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        UUID actorId = u.getUserId();
        UserRole role = u.getRole();

        ForumVisibilityStrategy strategy = visibilityStrategyFactory.forRole(role);

        Specification<Forum> spec = Specification.where(strategy.spec(params, actorId))
                .and(ForumSpecs.titleLike(params.getSearch()))
                .and(params.getAccessFilter() == AccessFilter.ALL ? null
                        : ForumSpecs.accessType(params.getAccessFilter().toForumAccessType()));


        if (params.getCommentsCount() != null) {
            spec = spec.and(ForumSpecs.withCommentsCountSort());
        } else if (params.getLikesCount() != null) {
            spec = spec.and(ForumSpecs.withLikesCountSort());
        }

        Pageable pageable = PageRequest.of(resolvePage(params.getPage()), resolveLimit(params.getLimit()),
                mapCreatedAtSort(params.getCreatedAt()));

        Page<Forum> page = forumRepository.findAll(spec, pageable);

        List<UUID> forumIds = page.getContent().stream().map(Forum::getId).toList();
        List<UUID> courseIds = page.getContent().stream().map(Forum::getCourseId).toList();
        List<UUID> ownerIds = page.getContent().stream().map(Forum::getOwnerId).toList();

        Map<UUID, Long> comments = forumCommentService.countByForumIds(forumIds);
        Map<UUID, Long> likes = forumLikeService.countByForumIds(forumIds);

        List<CourseCoverDto> coverDtos = courseClient.getCourseCovers(new CourseIdsRequest(courseIds));
        Map<UUID, String> cover = coverDtos.stream()
                .collect(Collectors.toMap(CourseCoverDto::getCourseId, CourseCoverDto::getCoverImageUrl));

        Map<UUID, ForumAuthorDto> authors = userClient.getAuthors(new ForumAuthorIdsRequest(ownerIds));

        List<ForumListItemResponseDto> data = page.getContent().stream()
                .map(forum -> forumListMapper.toListItem(
                        forum,
                        comments.getOrDefault(forum.getId(), 0L),
                        likes.getOrDefault(forum.getId(), 0L),
                        cover.get(forum.getCourseId()),
                        authors.get(forum.getOwnerId())
                ))
                .toList();


        log.debug("Found {} forums out of {} total", page.getNumberOfElements(), page.getTotalElements());
        return ForumListResponseDto.builder()
                .total(page.getTotalElements())
                .page(page.getNumber() + 1)
                .limit(page.getSize())
                .data(data)
                .build();
    }

    private int resolvePage(Integer page) {
        return (page == null || page < 1) ? 0 : page - 1;
    }

    private int resolveLimit(Integer limit) {
        return (limit == null || limit < 1) ? 20 : limit;
    }

    private Sort mapCreatedAtSort(CreatedAtSort s) {
        if (s == CreatedAtSort.OLDEST) return Sort.by("createdAt").ascending();
        return Sort.by("createdAt").descending();
    }
}

