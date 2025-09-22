package uz.consortgroup.forum_service.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uz.consortgroup.core.api.v1.dto.forum.enumeration.ForumAccessPolicy;
import uz.consortgroup.forum_service.entity.Forum;
import uz.consortgroup.forum_service.exception.ForumValidationException;
import uz.consortgroup.forum_service.service.CourseAccessService;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EnrollmentAccessStrategy implements ForumAccessStrategy {

    private final CourseAccessService courseAccessService;

    @Override
    public ForumAccessPolicy supports() {
        return ForumAccessPolicy.BY_ENROLLMENT;
    }

    @Override
    public void assertCanPost(Forum forum, UUID userId) {
        boolean active = courseAccessService.hasActiveAccess(userId, forum.getCourseId());
        if (!active) {
            throw new ForumValidationException("Forum is closed for this user (no active course access)");
        }
    }
}
