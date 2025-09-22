package uz.consortgroup.forum_service.strategy;

import uz.consortgroup.core.api.v1.dto.forum.enumeration.ForumAccessPolicy;
import uz.consortgroup.forum_service.entity.Forum;

import java.util.UUID;

public interface ForumAccessStrategy {
    ForumAccessPolicy supports();
    void assertCanPost(Forum forum, UUID userId);
}
