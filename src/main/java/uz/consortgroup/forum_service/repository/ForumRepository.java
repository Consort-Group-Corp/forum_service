package uz.consortgroup.forum_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.consortgroup.core.api.v1.dto.forum.enumeration.ForumAccessPolicy;
import uz.consortgroup.forum_service.entity.Forum;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ForumRepository extends JpaRepository<Forum, UUID>, JpaSpecificationExecutor<Forum> {

    Optional<Forum> findByCourseId(UUID courseId);

    @Query("""
                select f from Forum f
                where (:policy is null or f.accessPolicy = :policy)
                  and (:now between f.startTime and f.endTime)
                order by f.createdAt desc
            """)
    Page<Forum> findActive(
            @Param("policy") ForumAccessPolicy policy,
            @Param("now") Instant now,
            Pageable pageable);

    @Query("""
                select f from Forum f
                where (:title is null or lower(f.title) like lower(concat('%', :title, '%')))
                order by f.createdAt desc
            """)
    Page<Forum> searchByTitle(
            @Param("title") String title,
            Pageable pageable);

}
