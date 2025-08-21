package uz.consortgroup.forum_service.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.consortgroup.forum_service.repository.ForumLikeRepository;
import uz.consortgroup.forum_service.repository.IdCount;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
class ForumLikeServiceImpl implements ForumLikeService {
    private final ForumLikeRepository repo;

    @Override
    public Map<UUID, Long> countByForumIds(List<UUID> forumIds) {
        log.info("Counting likes by forumIds={}", forumIds);

        if (forumIds.isEmpty()) return Map.of();
        return repo.countLikesByForumIds(forumIds).stream()
                .collect(Collectors.toMap(IdCount::getId, IdCount::getCount));
    }
}