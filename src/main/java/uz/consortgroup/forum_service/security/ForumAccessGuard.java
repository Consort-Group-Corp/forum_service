package uz.consortgroup.forum_service.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uz.consortgroup.core.api.v1.dto.forum.enumeration.ForumAccessPolicy;
import uz.consortgroup.forum_service.entity.Forum;
import uz.consortgroup.forum_service.strategy.ForumAccessStrategy;
import uz.consortgroup.forum_service.strategy.ForumAccessStrategyResolver;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ForumAccessGuard {

    private final ForumAccessStrategyResolver resolver;

    public void assertCanPost(Forum forum, UUID userId) {
        ForumAccessPolicy policy = forum.getAccessPolicy();
        ForumAccessStrategy strategy = resolver.resolve(policy);
        strategy.assertCanPost(forum, userId);
    }
}
