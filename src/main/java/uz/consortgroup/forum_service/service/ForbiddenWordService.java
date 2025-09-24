package uz.consortgroup.forum_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.consortgroup.core.api.v1.dto.forum.request.ForbiddenWordCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.request.ForbiddenWordUpdateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.response.ForbiddenWordCheckResponseDto;
import uz.consortgroup.core.api.v1.dto.forum.response.ForbiddenWordResponseDto;

import java.util.UUID;

public interface ForbiddenWordService {
    ForbiddenWordResponseDto createForbiddenWord(ForbiddenWordCreateRequestDto dto);
    ForbiddenWordResponseDto updateForbiddenWord(UUID id, ForbiddenWordUpdateRequestDto dto);
    Page<ForbiddenWordResponseDto> getForbiddenWords(Pageable pageable, Boolean active);
    ForbiddenWordCheckResponseDto checkText(String text);
}
