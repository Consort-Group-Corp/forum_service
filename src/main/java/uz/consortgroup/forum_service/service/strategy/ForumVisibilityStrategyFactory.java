package uz.consortgroup.forum_service.service.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uz.consortgroup.core.api.v1.dto.user.enumeration.UserRole;

@Component
@RequiredArgsConstructor
public class ForumVisibilityStrategyFactory {
    private final AdminVisibilityStrategy admin;
    private final MentorVisibilityStrategy mentor;
    private final StudentVisibilityStrategy student;
    private final EmptyVisibilityStrategy none;

    public ForumVisibilityStrategy forRole(UserRole role) {
        return switch (role) {
            case SUPER_ADMIN, ADMIN, MODERATOR -> admin;
            case MENTOR -> mentor;
            case STUDENT -> student;
            default -> none;
        };
    }
}

