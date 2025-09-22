package uz.consortgroup.forum_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.forum.response.ForumLikeResponseDto;
import uz.consortgroup.forum_service.entity.Forum;
import uz.consortgroup.forum_service.entity.ForumLike;
import uz.consortgroup.forum_service.exception.ForumValidationException;
import uz.consortgroup.forum_service.repository.ForumLikeRepository;
import uz.consortgroup.forum_service.security.AuthContext;
import uz.consortgroup.forum_service.service.ForumLikeService;
import uz.consortgroup.forum_service.validator.ForumLikeValidator;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ForumLikeServiceImpl implements ForumLikeService {

    private final ForumLikeRepository likeRepository;
    private final ForumLikeValidator likeValidator;
    private final AuthContext authContext;

    @Override
    @Transactional
    public ForumLikeResponseDto addLikeToForum(UUID forumId) {
        UUID userId = authContext.getCurrentUserId();

        log.info("Like forum: forumId={}, userId={}", forumId, userId);

        Forum forum = likeValidator.validate(forumId, Instant.now());

        boolean exists = likeRepository.existsByForum_IdAndUserId(forum.getId(), userId);
        if (exists) {
            throw new ForumValidationException("User already liked this forum");
        }

        ForumLike like = ForumLike.builder()
                .forum(forum)
                .userId(userId)
                .createdAt(Instant.now())
                .build();

        likeRepository.save(like);

        long count = likeRepository.countByForum_Id(forum.getId());
        return ForumLikeResponseDto.builder()
                .likeCount(count)
                .likedByMe(true)
                .build();
    }

    @Override
    @Transactional
    public ForumLikeResponseDto removeLikeFromForum(UUID forumId) {
        UUID userId = authContext.getCurrentUserId();

        log.info("Unlike forum: forumId={}, userId={}", forumId, userId);

        Forum forum = likeValidator.validate(forumId, Instant.now());

        likeRepository.findByForum_IdAndUserId(forum.getId(), userId)
                .ifPresent(likeRepository::delete);

        long count = likeRepository.countByForum_Id(forum.getId());
        return ForumLikeResponseDto.builder()
                .likeCount(count)
                .likedByMe(false)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ForumLikeResponseDto getForumLikeStatus(UUID forumId) {
        UUID userId = authContext.getCurrentUserId();
        long count = likeRepository.countByForum_Id(forumId);
        boolean liked = likeRepository.existsByForum_IdAndUserId(forumId, userId);

        return ForumLikeResponseDto.builder()
                .likeCount(count)
                .likedByMe(liked)
                .build();
    }
}
