package uz.consortgroup.forum_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.forum.enumeration.ComplaintStatus;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumComplaintCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumComplaintUpdateStatusRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.response.ForumComplaintResponseDto;
import uz.consortgroup.forum_service.entity.ForumComplaint;
import uz.consortgroup.forum_service.exception.ForumComplaintNotFoundException;
import uz.consortgroup.forum_service.mapper.ForumComplaintMapper;
import uz.consortgroup.forum_service.repository.ForumComplaintRepository;
import uz.consortgroup.forum_service.security.AuthContext;
import uz.consortgroup.forum_service.service.ForumComplaintService;
import uz.consortgroup.forum_service.validator.ForumComplaintValidator;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ForumComplaintServiceImpl implements ForumComplaintService {

    private final ForumComplaintRepository complaintRepository;
    private final ForumComplaintMapper complaintMapper;
    private final ForumComplaintValidator complaintValidator;
    private final AuthContext authContext;

    @Override
    @Transactional
    public ForumComplaintResponseDto create(ForumComplaintCreateRequestDto dto) {
        UUID reporterId = authContext.getCurrentUserId();

        log.info("Creating complaint: forumId={}, topicId={}, commentId={}, reporterId={}, offenderId={}, reason={}",
                dto.getForumId(), dto.getTopicId(), dto.getCommentId(), reporterId, dto.getOffenderId(), dto.getReason());

        ForumComplaintValidator.ValidationResult validationResult =
                complaintValidator.validateCreate(dto, Instant.now());

        ForumComplaint entity = complaintMapper.toEntityOnCreate(dto);
        entity.setForum(validationResult.forum());
        entity.setReporterId(reporterId);
        entity.setTopic(validationResult.topic());
        entity.setComment(validationResult.comment());
        entity.setStatus(ComplaintStatus.NEW);
        entity.setCreatedAt(Instant.now());

        ForumComplaint saved = complaintRepository.save(entity);
        ForumComplaintResponseDto response = complaintMapper.toDto(saved);

        log.info("Complaint created: id={}, status={}", saved.getId(), saved.getStatus());
        return response;
    }

    @Override
    @Transactional
    public ForumComplaintResponseDto updateStatus(UUID id, ForumComplaintUpdateStatusRequestDto dto) {
        UUID moderatorId = authContext.getCurrentUserId();

        log.info("Updating complaint status: id={}, newStatus={}, moderatorId={}",
                id, dto.getStatus(), moderatorId);

        ForumComplaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ForumComplaintNotFoundException(
                        String.format("Forum complaint with id %s not found", id)));

        complaintValidator.validateStatusUpdate(complaint, dto);

        complaint.setStatus(dto.getStatus());
        complaint.setResolvedAt(Instant.now());
        complaint.setResolvedBy(moderatorId);

        ForumComplaint saved = complaintRepository.save(complaint);
        ForumComplaintResponseDto response = complaintMapper.toDto(saved);

        log.info("Complaint status updated: id={}, status={}, resolvedAt={}, resolvedBy={}",
                saved.getId(), saved.getStatus(), saved.getResolvedAt(), saved.getResolvedBy());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ForumComplaintResponseDto getById(UUID id) {
        log.info("Fetching complaint: id={}", id);

        ForumComplaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ForumComplaintNotFoundException(
                        String.format("Forum complaint with id %s not found", id)));

        ForumComplaintResponseDto response = complaintMapper.toDto(complaint);
        log.debug("Complaint fetched: id={}, status={}", complaint.getId(), complaint.getStatus());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ForumComplaintResponseDto> list(Pageable pageable, ComplaintStatus status) {
        log.info("Listing complaints: status={}, pageNumber={}, pageSize={}",
                status, pageable.getPageNumber(), pageable.getPageSize());

        Page<ForumComplaint> page = complaintRepository.findByStatus(status, pageable);
        Page<ForumComplaintResponseDto> mapped = page.map(complaintMapper::toDto);

        log.debug("Complaints listed: totalElements={}, totalPages={}",
                mapped.getTotalElements(), mapped.getTotalPages());
        return mapped;
    }
}
