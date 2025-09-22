package uz.consortgroup.forum_service.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uz.consortgroup.core.api.v1.dto.forum.enumeration.ForumAccessPolicy;
import uz.consortgroup.forum_service.entity.Forum;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PublicAccessStrategy implements ForumAccessStrategy {

    @Override
    public ForumAccessPolicy supports() {
        return ForumAccessPolicy.PUBLIC;
    }

    @Override
    public void assertCanPost(Forum forum, UUID userId) {

    }
}
