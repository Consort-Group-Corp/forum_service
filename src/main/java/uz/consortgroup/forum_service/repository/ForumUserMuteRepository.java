package uz.consortgroup.forum_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.consortgroup.forum_service.entity.ForumUserMute;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface ForumUserMuteRepository extends JpaRepository<ForumUserMute, UUID> {
    boolean existsByUserIdAndMuteUntilAfter(UUID userId, Instant now);
    Page<ForumUserMute> findByForumId(UUID forumId, Pageable pageable);
}
