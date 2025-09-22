package uz.consortgroup.forum_service.service.strategy;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumListParamsRequestDto;
import uz.consortgroup.forum_service.entity.Forum;
import uz.consortgroup.forum_service.service.ForumVisibilityStrategy;

import java.util.UUID;

@Component
class EmptyVisibilityStrategy implements ForumVisibilityStrategy {

    @Override
    public Specification<Forum> spec(ForumListParamsRequestDto p, UUID actorId) {
        return (root, cq, cb) -> cb.disjunction();
    }
}