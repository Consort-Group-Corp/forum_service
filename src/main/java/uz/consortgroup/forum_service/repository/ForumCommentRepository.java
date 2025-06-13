package uz.consortgroup.forum_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.consortgroup.forum_service.entity.ForumComment;

import java.util.UUID;

public interface ForumCommentRepository extends JpaRepository<ForumComment, UUID> {
}
