package uz.consortgroup.forum_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uz.consortgroup.forum_service.entity.Forum;
import uz.consortgroup.forum_service.exception.ForumValidationException;
import uz.consortgroup.forum_service.security.ForumAccessGuard;
import uz.consortgroup.forum_service.service.ModerationService;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthorPostingGuard {

    private final ForumAccessGuard forumAccessGuard;
    private final ModerationService moderationService;

    public void ensureForumWindowActive(Forum forum, Instant now) {
        if (now.isBefore(forum.getStartTime()) || now.isAfter(forum.getEndTime())) {
            throw new ForumValidationException("Forum is not active in current time window");
        }
    }

    public void assertAuthorAllowedToPost(Forum forum, UUID authorId) {
        forumAccessGuard.assertCanPost(forum, authorId);
        if (moderationService.isUserBlocked(authorId)) {
            throw new ForumValidationException("User is blocked");
        }
        if (moderationService.isUserMuted(authorId)) {
            throw new ForumValidationException("User is muted");
        }
    }
}
