package uz.consortgroup.forum_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.consortgroup.forum_service.entity.Forum;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ForumRepository extends JpaRepository<Forum, UUID> {
    @Query("SELECT f.groupId FROM Forum f WHERE f.courseId = :courseId")
    Optional<UUID> findGroupIdByCourseId(@Param("courseId") UUID courseId);
}
