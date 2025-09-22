package uz.consortgroup.forum_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.consortgroup.core.api.v1.dto.user.response.CourseAccessResponse;
import uz.consortgroup.forum_service.client.UserAccessClient;
import uz.consortgroup.forum_service.service.CourseAccessService;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseAccessServiceImpl implements CourseAccessService {

    private final UserAccessClient userAccessClient;

    @Override
    public boolean hasActiveAccess(UUID userId, UUID courseId) {
        CourseAccessResponse resp = userAccessClient.checkAccess(userId, courseId);
        return resp != null && resp.isHasAccess();
    }
}
