package uz.consortgroup.forum_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.consortgroup.forum_service.entity.Forum;

import java.util.UUID;

@Repository
public interface ForumRepository extends JpaRepository<Forum, UUID>, JpaSpecificationExecutor<Forum> {
}
