package uz.consortgroup.forum_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.consortgroup.core.api.v1.dto.forum.enumeration.ComplaintStatus;
import uz.consortgroup.forum_service.entity.ForumComplaint;

import java.util.UUID;

@Repository
public interface ForumComplaintRepository extends JpaRepository<ForumComplaint, UUID> {

    @Query("""
        select c from ForumComplaint c
        where (:status is null or c.status = :status)
        order by c.createdAt desc
    """)
    Page<ForumComplaint> findByStatus(@Param("status") ComplaintStatus status, Pageable pageable);
}
