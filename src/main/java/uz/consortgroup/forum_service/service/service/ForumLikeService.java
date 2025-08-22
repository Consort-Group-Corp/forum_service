package uz.consortgroup.forum_service.service.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ForumLikeService {
    Map<UUID, Long> countByForumIds(List<UUID> forumIds);
}
