package uz.consortgroup.forum_service.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumUpdateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.response.ForumResponseDto;
import uz.consortgroup.forum_service.entity.Forum;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ForumMapper {

    @Mapping(target = "groupId", expression = "java(forum.getGroup() != null ? forum.getGroup().getId() : null)")
    @Mapping(target = "forumAccessPolicy", source = "accessPolicy")
    ForumResponseDto toDto(Forum forum);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "courseId", source = "courseId")
    @Mapping(target = "accessPolicy", source = "forumAccessPolicy")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "startTime", source = "startTime")
    @Mapping(target = "endTime", source = "endTime")
    Forum toEntityOnCreate(ForumCreateRequestDto dto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "title", source = "title")
    @Mapping(target = "accessPolicy", source = "forumAccessPolicy")
    @Mapping(target = "startTime", source = "startTime")
    @Mapping(target = "endTime", source = "endTime")
    Forum updateEntityFromDto(ForumUpdateRequestDto dto, @MappingTarget Forum forum);

    @AfterMapping
    default void trimTitleCreate(ForumCreateRequestDto dto, @MappingTarget Forum forum) {
        if (dto.getTitle() != null) {
            forum.setTitle(dto.getTitle().trim());
        }
    }

    @AfterMapping
    default void trimTitleUpdate(ForumUpdateRequestDto dto, @MappingTarget Forum forum) {
        if (dto.getTitle() != null) {
            forum.setTitle(dto.getTitle().trim());
        }
    }
}
