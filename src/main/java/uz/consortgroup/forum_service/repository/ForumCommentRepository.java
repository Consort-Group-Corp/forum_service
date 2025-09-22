package uz.consortgroup.forum_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.consortgroup.forum_service.entity.ForumComment;

import java.util.List;
import java.util.UUID;

@Repository
public interface ForumCommentRepository extends JpaRepository<ForumComment, UUID> {

    @Query("""
                select ft.forum.id as id, count(fc.id) as count
                from ForumComment fc
                join fc.forumTopic ft
                where ft.forum.id in :forumIds
                group by ft.forum.id
            """)
    List<IdCount> countCommentsByForumIds(@Param("forumIds") List<UUID> forumIds);

    @Query("select count(c) from ForumComment c")
    Long countAllComments();

    Page<ForumComment> findByForumTopic_IdOrderByCreatedAtAsc(UUID topicId, Pageable pageable);

    @Query("SELECT COUNT(c) FROM ForumComment c WHERE c.forumTopic.forum.id = :forumId")
    long countByForumId(@Param("forumId") UUID forumId);

    long countByForumTopic_Id(UUID topicId);
}
