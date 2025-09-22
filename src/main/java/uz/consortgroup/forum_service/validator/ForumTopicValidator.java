package uz.consortgroup.forum_service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uz.consortgroup.core.api.v1.dto.forum.enumeration.LanguageCode;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumTopicCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumTopicUpdateRequestDto;
import uz.consortgroup.forum_service.entity.Forum;
import uz.consortgroup.forum_service.exception.ForumNotFoundException;
import uz.consortgroup.forum_service.exception.ForumValidationException;
import uz.consortgroup.forum_service.repository.ForumRepository;
import uz.consortgroup.forum_service.security.AuthContext;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ForumTopicValidator {

    private final ForumRepository forumRepository;
    private final AuthorPostingGuard authorGuard;
    private final ContentPolicyChecker contentChecker;
    private final AuthContext authContext;

    public Forum validateCreate(UUID forumId, ForumTopicCreateRequestDto dto, Instant now) {
        UUID authorId = authContext.getCurrentUserId();

        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new ForumNotFoundException("Forum with id " + forumId + " not found"));

        authorGuard.ensureForumWindowActive(forum, now);
        authorGuard.assertAuthorAllowedToPost(forum, authorId);

        ensureLanguageValid(dto.getLanguageCode());
        contentChecker.ensureNoForbidden(dto.getTitle(), dto.getContent());
        return forum;
    }

    public void validateUpdate(Forum forum, ForumTopicUpdateRequestDto dto, Instant now) {
        authorGuard.ensureForumWindowActive(forum, now);
        if (dto.getLanguageCode() != null) ensureLanguageValid(dto.getLanguageCode());
        contentChecker.ensureNoForbidden(dto.getTitle(), dto.getContent());
    }

    private void ensureLanguageValid(LanguageCode code) {
        if (code == null) throw new ForumValidationException("Invalid language code");
    }
}
