package uz.consortgroup.forum_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import uz.consortgroup.core.api.v1.dto.forum.request.ForumComplaintCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.response.ForumComplaintResponseDto;
import uz.consortgroup.forum_service.entity.ForumComplaint;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ForumComplaintMapper {

    @Mapping(target = "forumId", source = "forum.id")
    @Mapping(target = "topicId", source = "topic.id")
    @Mapping(target = "commentId", source = "comment.id")
    ForumComplaintResponseDto toDto(ForumComplaint entity);

    @Mapping(target = "offenderId", source = "offenderId")
    @Mapping(target = "reason", source = "reason")
    @Mapping(target = "messageSnapshot", source = "messageSnapshot")
    ForumComplaint toEntityOnCreate(ForumComplaintCreateRequestDto dto);
}
