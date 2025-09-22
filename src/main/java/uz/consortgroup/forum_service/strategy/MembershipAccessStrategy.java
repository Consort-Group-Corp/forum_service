package uz.consortgroup.forum_service.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uz.consortgroup.core.api.v1.dto.forum.enumeration.ForumAccessPolicy;
import uz.consortgroup.forum_service.entity.Forum;
import uz.consortgroup.forum_service.exception.ForumValidationException;
import uz.consortgroup.forum_service.repository.ForumUserGroupMembershipRepository;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MembershipAccessStrategy implements ForumAccessStrategy {

    private final ForumUserGroupMembershipRepository membershipRepo;

    @Override
    public ForumAccessPolicy supports() {
        return ForumAccessPolicy.BY_MEMBERSHIP;
    }

    @Override
    public void assertCanPost(Forum forum, UUID userId) {
        if (forum.getGroup() == null) {
            throw new ForumValidationException("Forum group is not configured");
        }
        boolean member = membershipRepo.existsByUserIdAndGroup_Id(userId, forum.getGroup().getId());
        if (!member) {
            throw new ForumValidationException("Forum is restricted to members of the group");
        }
    }
}
