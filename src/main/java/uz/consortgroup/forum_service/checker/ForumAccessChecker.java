package uz.consortgroup.forum_service.checker;

import java.util.UUID;

public interface ForumAccessChecker {
    void checkAccessOrThrow(UUID userId, UUID courseId);
}
