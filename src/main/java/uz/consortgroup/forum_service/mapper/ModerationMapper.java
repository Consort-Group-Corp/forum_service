package uz.consortgroup.forum_service.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import uz.consortgroup.core.api.v1.dto.forum.moderation.UserBlockCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.moderation.UserMuteCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.moderation.response.UserBlockResponseDto;
import uz.consortgroup.core.api.v1.dto.forum.moderation.response.UserMuteResponseDto;
import uz.consortgroup.forum_service.entity.ForumUserBlock;
import uz.consortgroup.forum_service.entity.ForumUserMute;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ModerationMapper {

    UserBlockResponseDto toDto(ForumUserBlock e);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "userId",   source = "userId")
    @Mapping(target = "reason",   source = "reason")
    ForumUserBlock toBlockEntity(UserBlockCreateRequestDto dto);

    UserMuteResponseDto toDto(ForumUserMute e);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "userId",    source = "userId")
    @Mapping(target = "reason",    source = "reason")
    @Mapping(target = "muteUntil", source = "muteUntil")
    ForumUserMute toMuteEntity(UserMuteCreateRequestDto dto);

}
