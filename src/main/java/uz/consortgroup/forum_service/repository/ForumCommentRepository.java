package uz.consortgroup.forum_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.consortgroup.forum_service.entity.ForumComment;

import java.util.List;
import java.util.UUID;

public interface ForumCommentRepository extends JpaRepository<ForumComment, UUID> {
    @Query("""
        select ft.forum.id as id, count(fc.id) as count
        from ForumComment fc
        join fc.forumTopic ft
        where ft.forum.id in :forumIds
        group by ft.forum.id
    """)
    List<IdCount> countCommentsByForumIds(@Param("forumIds") List<UUID> forumIds);
}
