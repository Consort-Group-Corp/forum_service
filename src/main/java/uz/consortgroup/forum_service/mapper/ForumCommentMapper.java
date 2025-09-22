package uz.consortgroup.forum_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumCommentCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumCommentUpdateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.response.ForumCommentResponseDto;
import uz.consortgroup.forum_service.entity.ForumComment;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ForumCommentMapper {

    @Mapping(target = "topicId", source = "forumTopic.id")
    @Mapping(target = "forumId", source = "forumTopic.forum.id")
    ForumCommentResponseDto toDto(ForumComment comment);

    @Mapping(target = "content",  source = "content")
    ForumComment toEntityOnCreate(ForumCommentCreateRequestDto dto);

    @Mapping(target = "content", source = "content")
    ForumComment updateEntityFromDto(ForumCommentUpdateRequestDto dto, @MappingTarget ForumComment comment);
}
