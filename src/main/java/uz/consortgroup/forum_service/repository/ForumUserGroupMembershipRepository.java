package uz.consortgroup.forum_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.consortgroup.forum_service.entity.ForumUserGroupMembership;

import java.util.UUID;

@Repository
public interface ForumUserGroupMembershipRepository extends JpaRepository<ForumUserGroupMembership, UUID> {
    boolean existsByUserIdAndGroup_Id(UUID userId, UUID groupId);
    long countByGroup_Id(UUID groupId);
}
