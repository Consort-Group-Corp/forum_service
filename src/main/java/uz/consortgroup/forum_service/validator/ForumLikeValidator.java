package uz.consortgroup.forum_service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uz.consortgroup.forum_service.entity.Forum;
import uz.consortgroup.forum_service.exception.ForumNotFoundException;
import uz.consortgroup.forum_service.exception.ForumValidationException;
import uz.consortgroup.forum_service.repository.ForumRepository;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ForumLikeValidator {

    private final ForumRepository forumRepository;

    public Forum validate(UUID forumId, Instant now) {
        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new ForumNotFoundException(
                        String.format("Forum with id %s not found", forumId)));

        if (now.isBefore(forum.getStartTime()) || now.isAfter(forum.getEndTime())) {
            throw new ForumValidationException("Forum is not active in current time window");
        }
        return forum;
    }
}
