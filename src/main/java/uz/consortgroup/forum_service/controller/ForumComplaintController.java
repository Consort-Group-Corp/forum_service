package uz.consortgroup.forum_service.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uz.consortgroup.core.api.v1.dto.forum.enumeration.ComplaintStatus;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumComplaintCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumComplaintUpdateStatusRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.response.ForumComplaintResponseDto;
import uz.consortgroup.forum_service.service.ForumComplaintService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/forum/complaints")
@RequiredArgsConstructor
@Tag(name = "Forum Complaint", description = "Жалоба на форум")
public class ForumComplaintController {

    private final ForumComplaintService complaintService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ForumComplaintResponseDto create(@Valid @RequestBody ForumComplaintCreateRequestDto dto) {
        return complaintService.create(dto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}/status")
    public ForumComplaintResponseDto updateStatus(@PathVariable UUID id, @Valid @RequestBody ForumComplaintUpdateStatusRequestDto dto) {
        return complaintService.updateStatus(id, dto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public ForumComplaintResponseDto getById(@PathVariable UUID id) {
        return complaintService.getById(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Page<ForumComplaintResponseDto> list(Pageable pageable,
                                                @RequestParam(required = false) ComplaintStatus status) {
        return complaintService.list(pageable, status);
    }
}
