package uz.consortgroup.forum_service.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import uz.consortgroup.core.api.v1.dto.forum.response.ForumTopicResponseDto;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumTopicCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumTopicUpdateRequestDto;
import uz.consortgroup.forum_service.entity.ForumTopic;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ForumTopicMapper {

    @Mapping(target = "forumId", source = "forum.id")
    ForumTopicResponseDto toDto(ForumTopic topic);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "title",         source = "title")
    @Mapping(target = "content",       source = "content")
    @Mapping(target = "languageCode",  source = "languageCode")
    @Mapping(target = "lessonRefType", source = "lessonRefType")
    @Mapping(target = "lessonRefId",   source = "lessonRefId")
    ForumTopic toEntityOnCreate(ForumTopicCreateRequestDto dto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "title",         source = "title")
    @Mapping(target = "content",       source = "content")
    @Mapping(target = "languageCode",  source = "languageCode")
    @Mapping(target = "lessonRefType", source = "lessonRefType")
    @Mapping(target = "lessonRefId",   source = "lessonRefId")
    ForumTopic updateEntityFromDto(ForumTopicUpdateRequestDto dto, @MappingTarget ForumTopic topic);

    @AfterMapping
    default void trimTitleCreate(ForumTopicCreateRequestDto dto, @MappingTarget ForumTopic topic) {
        if (dto.getTitle() != null) topic.setTitle(dto.getTitle().trim());
    }

    @AfterMapping
    default void trimTitleUpdate(ForumTopicUpdateRequestDto dto, @MappingTarget ForumTopic topic) {
        if (dto.getTitle() != null) topic.setTitle(dto.getTitle().trim());
    }
}
