package uz.consortgroup.forum_service.service.strategy;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import uz.consortgroup.core.api.v1.dto.forum.ForumListParamsRequestDto;
import uz.consortgroup.forum_service.entity.Forum;
import uz.consortgroup.forum_service.repository.spec.ForumSpecs;

import java.util.UUID;

@Component
class MentorVisibilityStrategy implements ForumVisibilityStrategy {

    @Override
    public Specification<Forum> spec(ForumListParamsRequestDto p, UUID actorId) {
        return ForumSpecs.owner(actorId);
    }
}