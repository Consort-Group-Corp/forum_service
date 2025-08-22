package uz.consortgroup.forum_service.service.strategy;

import org.springframework.data.jpa.domain.Specification;
import uz.consortgroup.core.api.v1.dto.forum.ForumListParamsRequestDto;
import uz.consortgroup.forum_service.entity.Forum;

import java.util.UUID;

public interface ForumVisibilityStrategy {
    Specification<Forum> spec(ForumListParamsRequestDto params, UUID actorId);
}