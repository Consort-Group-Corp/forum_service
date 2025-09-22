package uz.consortgroup.forum_service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uz.consortgroup.core.api.v1.dto.forum.enumeration.ForumAccessPolicy;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumUpdateRequestDto;
import uz.consortgroup.forum_service.entity.Forum;
import uz.consortgroup.forum_service.entity.ForumUserGroup;
import uz.consortgroup.forum_service.exception.ForumUserGroupNotFoundException;
import uz.consortgroup.forum_service.exception.ForumValidationException;
import uz.consortgroup.forum_service.repository.ForumUserGroupRepository;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ForumValidator {

    private final ForumUserGroupRepository groupRepository;

    public ForumUserGroup validateCreateAndResolveGroup(ForumCreateRequestDto dto) {
        log.debug("Validating forum create DTO: {}", dto);

        Instant start = dto.getStartTime();
        Instant end = dto.getEndTime();
        ensureStartBeforeEnd(start, end);

        ForumAccessPolicy policy = dto.getForumAccessPolicy();
        UUID groupId = dto.getGroupId();

        if (policy == ForumAccessPolicy.BY_MEMBERSHIP) {
            log.debug("CREATE: BY_MEMBERSHIP policy, resolving groupId={}", groupId);
            return resolveUserGroupForMembershipPolicy(groupId);
        }
        log.debug("CREATE: policy={}, no group needed", policy);
        return null;
    }

    public ForumUserGroup validateUpdateAndResolveGroup(Forum forum, ForumUpdateRequestDto dto) {
        log.debug("Validating forum update. Forum ID: {}, DTO: {}", forum.getId(), dto);

        validateTimeRange(forum, dto);

        ForumAccessPolicy policy = dto.getForumAccessPolicy();
        UUID dtoGroupId = dto.getGroupId();

        if (policy == ForumAccessPolicy.BY_MEMBERSHIP) {
            log.debug("UPDATE: BY_MEMBERSHIP policy for forum ID: {}", forum.getId());
            return resolveUserGroupForMembershipPolicy(dtoGroupId);
        } else {
            log.debug("UPDATE: no group validation needed for policy: {}", policy);
            return null;
        }
    }

    private void validateTimeRange(Forum forum, ForumUpdateRequestDto dto) {
        Instant startTime = dto.getStartTime() != null ? dto.getStartTime() : forum.getStartTime();
        Instant endTime = dto.getEndTime() != null ? dto.getEndTime() : forum.getEndTime();
        log.debug("Validating time range. Start: {}, End: {}", startTime, endTime);
        ensureStartBeforeEnd(startTime, endTime);
    }

    private ForumUserGroup resolveUserGroupForMembershipPolicy(UUID groupId) {
        if (groupId == null) {
            log.warn("Validation failed: groupId is required for BY_MEMBERSHIP policy");
            throw new ForumValidationException("groupId is required for access policy BY_MEMBERSHIP");
        }
        log.debug("Resolving ForumUserGroup by id={}", groupId);
        return groupRepository.findById(groupId)
                .orElseThrow(() -> {
                    String msg = String.format("Forum user group with id %s not found", groupId);
                    log.warn("Validation failed: {}", msg);
                    return new ForumUserGroupNotFoundException(msg);
                });
    }

    private void ensureStartBeforeEnd(Instant start, Instant end) {
        if (start != null && end != null && !start.isBefore(end)) {
            log.warn("Validation failed: start_time {} must be before end_time {}", start, end);
            throw new ForumValidationException("start_time must be before end_time");
        }
        if (start == null || end == null) {
            log.debug("Time validation skipped: one of the values is null (start: {}, end: {})", start, end);
        }
    }
}
