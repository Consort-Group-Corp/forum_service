package uz.consortgroup.forum_service.service.service;

import uz.consortgroup.core.api.v1.dto.forum.ForumListParamsRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.ForumListResponseDto;

public interface ForumListQueryService {
    ForumListResponseDto list(ForumListParamsRequestDto params);
}