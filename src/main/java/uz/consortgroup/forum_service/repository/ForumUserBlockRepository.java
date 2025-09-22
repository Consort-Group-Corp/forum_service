package uz.consortgroup.forum_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.consortgroup.forum_service.entity.ForumUserBlock;

import java.util.UUID;

@Repository
public interface ForumUserBlockRepository extends JpaRepository<ForumUserBlock, UUID> {
    boolean existsByUserId(UUID userId);
    Page<ForumUserBlock> findByForumId(UUID forumId, Pageable pageable);
    long countByForumIdAndUserId(UUID forumId, UUID userId);
}
