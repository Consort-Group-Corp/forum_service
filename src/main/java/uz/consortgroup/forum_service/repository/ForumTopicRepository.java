package uz.consortgroup.forum_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.consortgroup.core.api.v1.dto.forum.enumeration.LanguageCode;
import uz.consortgroup.forum_service.entity.ForumTopic;

import java.util.UUID;

@Repository
public interface ForumTopicRepository extends JpaRepository<ForumTopic, UUID> {

    @Query("""
       select t
       from ForumTopic t
       where t.forum.id = :forumId
         and (:lang is null or t.languageCode = :lang)
         and (:lessonId is null or t.lessonRefId = :lessonId)
       order by t.createdAt desc
    """)
    Page<ForumTopic> findByFilters(
            @Param("forumId") UUID forumId,
            @Param("lang") LanguageCode lang,
            @Param("lessonId") UUID lessonId,
            Pageable pageable);

    long countByForum_Id(UUID forumId);
}
