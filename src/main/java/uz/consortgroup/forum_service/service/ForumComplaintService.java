package uz.consortgroup.forum_service.service;

import org.springframework.data.domain.Page;
import uz.consortgroup.core.api.v1.dto.forum.enumeration.ComplaintStatus;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumComplaintCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumComplaintUpdateStatusRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.response.ForumComplaintResponseDto;

import java.util.UUID;

public interface ForumComplaintService {
    ForumComplaintResponseDto create(ForumComplaintCreateRequestDto dto);
    ForumComplaintResponseDto updateStatus(UUID id, ForumComplaintUpdateStatusRequestDto dto);
    ForumComplaintResponseDto getById(UUID id);
    Page<ForumComplaintResponseDto> list(org.springframework.data.domain.Pageable pageable, ComplaintStatus status);
}
