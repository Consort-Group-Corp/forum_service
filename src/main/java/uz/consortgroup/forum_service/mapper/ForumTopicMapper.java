package uz.consortgroup.forum_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uz.consortgroup.core.api.v1.dto.forum.ForumTopicResponse;
import uz.consortgroup.forum_service.entity.ForumTopic;

@Mapper(componentModel = "spring",  unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ForumTopicMapper {
    @Mapping(target = "forumId", source = "forum.id")
    ForumTopicResponse toDto(ForumTopic forumTopic);
}
