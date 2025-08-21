package uz.consortgroup.forum_service.repository.spec;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import uz.consortgroup.core.api.v1.dto.user.enumeration.ForumAccessType;
import uz.consortgroup.forum_service.entity.Forum;
import uz.consortgroup.forum_service.entity.ForumComment;
import uz.consortgroup.forum_service.entity.ForumLike;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class ForumSpecs {
    private ForumSpecs() {}

    public static Specification<Forum> titleLike(String q) {
        if (q == null || q.isBlank()) return null;
        String pattern = "%" + q.trim().toLowerCase() + "%";
        return (root, cq, cb) -> cb.like(cb.lower(root.get("title")), pattern);
    }

    public static Specification<Forum> accessType(ForumAccessType type) {
        if (type == null) return null;
        return (root, cq, cb) -> cb.equal(root.get("forumAccessType"), type);
    }

    public static Specification<Forum> owner(UUID tutorId) {
        if (tutorId == null) return null;
        return (root, cq, cb) -> cb.equal(root.get("ownerId"), tutorId);
    }

    public static Specification<Forum> groupIds(List<UUID> groupIds) {
        if (groupIds == null || groupIds.isEmpty()) return (r,cq,cb) -> cb.disjunction();
        return (root, cq, cb) -> root.get("groupId").in(groupIds);
    }

    public static Specification<Forum> withCommentsCountSort() {
        return (root, query, cb) -> {
            if (Objects.requireNonNull(query).getResultType() != Long.class) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<ForumComment> commentRoot = subquery.from(ForumComment.class);
                subquery.select(cb.count(commentRoot));
                subquery.where(cb.equal(commentRoot.get("forum").get("id"), root.get("id")));

                query.orderBy(cb.asc(subquery));
            }
            return null;
        };
    }

    public static Specification<Forum> withLikesCountSort() {
        return (root, query, cb) -> {
            if (Objects.requireNonNull(query).getResultType() != Long.class) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<ForumLike> likeRoot = subquery.from(ForumLike.class);
                subquery.select(cb.count(likeRoot));
                subquery.where(cb.equal(likeRoot.get("forum").get("id"), root.get("id")));

                query.orderBy(cb.asc(subquery));
            }
            return null;
        };
    }
}
