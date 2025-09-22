package uz.consortgroup.forum_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.consortgroup.forum_service.entity.ForumLike;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ForumLikeRepository extends JpaRepository<ForumLike, UUID> {
    boolean existsByForum_IdAndUserId(UUID forumId, UUID userId);
    long countByForum_Id(UUID forumId);
    Optional<ForumLike> findByForum_IdAndUserId(UUID forumId, UUID userId);
}
