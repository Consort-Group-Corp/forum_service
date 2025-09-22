package uz.consortgroup.forum_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import uz.consortgroup.core.api.v1.dto.forum.request.ForbiddenWordCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.request.ForbiddenWordUpdateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.response.ForbiddenWordResponseDto;
import uz.consortgroup.forum_service.entity.ForumForbiddenWord;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ForbiddenWordMapper {

    ForbiddenWordResponseDto toDto(ForumForbiddenWord entity);

    @Mapping(target = "word", source = "word")
    @Mapping(target = "createdBy", source = "createdBy")
    ForumForbiddenWord toEntityOnCreate(ForbiddenWordCreateRequestDto dto);

    @Mapping(target = "word", source = "word")
    @Mapping(target = "active", source = "active")
    ForumForbiddenWord updateEntityFromDto(ForbiddenWordUpdateRequestDto dto, @MappingTarget ForumForbiddenWord entity);
}
