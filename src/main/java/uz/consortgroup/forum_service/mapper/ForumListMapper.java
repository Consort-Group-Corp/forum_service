package uz.consortgroup.forum_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uz.consortgroup.core.api.v1.dto.forum.ForumAuthorDto;
import uz.consortgroup.core.api.v1.dto.forum.ForumListItemResponseDto;
import uz.consortgroup.forum_service.entity.Forum;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ForumListMapper {

    @Mapping(target = "id", source = "forum.id")
    @Mapping(target = "title", source = "forum.title")
    @Mapping(target = "author", source = "authorDto")
    @Mapping(target = "createdAt", source = "forum.createdAt")
    @Mapping(target = "commentsCount", source = "commentsCount")
    @Mapping(target = "likesCount", source = "likesCount")
    @Mapping(target = "accessType", source = "forum.forumAccessType")
    @Mapping(target = "previewImageUrl", source = "coverUrl")
    ForumListItemResponseDto toListItem(Forum forum,
                                        Long commentsCount,
                                        Long likesCount,
                                        String coverUrl,
                                        ForumAuthorDto authorDto);
}