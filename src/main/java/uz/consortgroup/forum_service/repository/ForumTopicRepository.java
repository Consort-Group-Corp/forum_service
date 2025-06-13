package uz.consortgroup.forum_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.consortgroup.forum_service.entity.ForumTopic;

import java.util.UUID;

@Repository
public interface ForumTopicRepository extends JpaRepository<ForumTopic, UUID> {
}
