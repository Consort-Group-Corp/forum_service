package uz.consortgroup.forum_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.consortgroup.forum_service.entity.ForumLike;

import java.util.List;
import java.util.UUID;

@Repository
public interface ForumLikeRepository extends JpaRepository<ForumLike, UUID> {
    @Query("""
              select fl.forum.id as id, count(fl.id) as count
              from ForumLike fl
              where fl.forum.id in :forumIds
              group by fl.forum.id
            """)
    List<IdCount> countLikesByForumIds(@Param("forumIds") List<UUID> forumIds);

    boolean existsByForum_IdAndUserId(UUID forumId, UUID userId);
}
