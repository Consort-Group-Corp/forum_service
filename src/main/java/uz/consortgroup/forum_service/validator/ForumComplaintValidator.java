package uz.consortgroup.forum_service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uz.consortgroup.core.api.v1.dto.forum.enumeration.ComplaintStatus;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumComplaintCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumComplaintUpdateStatusRequestDto;
import uz.consortgroup.forum_service.entity.Forum;
import uz.consortgroup.forum_service.entity.ForumComment;
import uz.consortgroup.forum_service.entity.ForumComplaint;
import uz.consortgroup.forum_service.entity.ForumTopic;
import uz.consortgroup.forum_service.exception.*;
import uz.consortgroup.forum_service.repository.ForumCommentRepository;
import uz.consortgroup.forum_service.repository.ForumTopicRepository;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ForumComplaintValidator {

    private final ForumTopicRepository topicRepository;
    private final ForumCommentRepository commentRepository;

    public ValidationResult validateCreate(ForumComplaintCreateRequestDto dto, Instant now) {
        ForumTopic topic = null;
        ForumComment comment = null;
        Forum forum = null;

        if (dto.getTopicId() != null) {
            topic = topicRepository.findById(dto.getTopicId())
                    .orElseThrow(() -> new ForumTopicNotFoundException(
                            String.format("Forum topic with id %s not found", dto.getTopicId())));
            forum = topic.getForum();
        }

        if (dto.getCommentId() != null) {
            comment = commentRepository.findById(dto.getCommentId())
                    .orElseThrow(() -> new ForumCommentNotFoundException(
                            String.format("Forum comment with id %s not found", dto.getCommentId())));

            forum = comment.getForumTopic().getForum();

            if (topic != null && !comment.getForumTopic().getId().equals(topic.getId())) {
                throw new ForumValidationException("Comment does not belong to the specified topic");
            }
        }

        if (forum == null) {
            throw new ForumValidationException("Either topic or comment must be provided");
        }

        if (now.isBefore(forum.getStartTime()) || now.isAfter(forum.getEndTime())) {
            throw new ForumValidationException("Forum is not active in current time window");
        }

        return new ValidationResult(forum, topic, comment);
    }

    public void validateStatusUpdate(ForumComplaint complaint, ForumComplaintUpdateStatusRequestDto dto) {
        if (complaint.getStatus() == ComplaintStatus.RESOLVED || complaint.getStatus() == ComplaintStatus.REJECTED) {
            throw new ForumValidationException("Complaint is already closed");
        }
    }

    public record ValidationResult(Forum forum, ForumTopic topic, ForumComment comment) {}
}