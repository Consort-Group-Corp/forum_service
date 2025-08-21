package uz.consortgroup.forum_service.service.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import uz.consortgroup.core.api.v1.dto.forum.ForumListParamsRequestDto;
import uz.consortgroup.forum_service.client.UserClient;
import uz.consortgroup.forum_service.entity.Forum;
import uz.consortgroup.forum_service.repository.spec.ForumSpecs;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class StudentVisibilityStrategy implements ForumVisibilityStrategy {
    private final UserClient userClient;

    @Override
    public Specification<Forum> spec(ForumListParamsRequestDto p, UUID actorId) {
        List<UUID> groupIds = userClient.getGroupIdsForUser(actorId);
        return ForumSpecs.groupIds(groupIds);
    }
}