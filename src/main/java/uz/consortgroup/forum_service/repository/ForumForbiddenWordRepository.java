package uz.consortgroup.forum_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.consortgroup.forum_service.entity.ForumForbiddenWord;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ForumForbiddenWordRepository extends JpaRepository<ForumForbiddenWord, UUID> {
    Optional<ForumForbiddenWord> findByWordIgnoreCase(String word);
    boolean existsByWordIgnoreCase(String word);
    Page<ForumForbiddenWord> findByActive(boolean active, Pageable pageable);
}
