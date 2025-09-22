package uz.consortgroup.forum_service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumCommentCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumCommentUpdateRequestDto;
import uz.consortgroup.forum_service.entity.Forum;
import uz.consortgroup.forum_service.entity.ForumTopic;
import uz.consortgroup.forum_service.exception.ForumTopicNotFoundException;
import uz.consortgroup.forum_service.repository.ForumTopicRepository;
import uz.consortgroup.forum_service.security.AuthContext;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ForumCommentValidator {

    private final ForumTopicRepository topicRepository;
    private final AuthorPostingGuard authorGuard;
    private final ContentPolicyChecker contentChecker;
    private final AuthContext authContext;

    public ForumTopic validateCreate(UUID topicId, ForumCommentCreateRequestDto dto, Instant now) {
        UUID authorId = authContext.getCurrentUserId();

        ForumTopic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ForumTopicNotFoundException("Forum topic with id " + topicId + " not found"));

        Forum forum = topic.getForum();

        authorGuard.ensureForumWindowActive(forum, now);
        authorGuard.assertAuthorAllowedToPost(forum, authorId);

        contentChecker.ensureNoForbidden(dto.getContent());

        return topic;
    }

    public void validateUpdate(ForumTopic topic, ForumCommentUpdateRequestDto dto, Instant now) {
        Forum forum = topic.getForum();
        authorGuard.ensureForumWindowActive(forum, now);
        if (dto.getContent() != null) {
            contentChecker.ensureNoForbidden(dto.getContent());
        }
    }
}
