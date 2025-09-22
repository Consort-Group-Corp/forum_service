package uz.consortgroup.forum_service.service;

import java.util.UUID;

public interface CourseAccessService {
    boolean hasActiveAccess(UUID userId, UUID courseId);
}
