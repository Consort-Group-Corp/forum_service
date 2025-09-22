package uz.consortgroup.forum_service.service.impl.moderation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.forum.moderation.response.ModerationUserInfoResponseDto;
import uz.consortgroup.core.api.v1.dto.forum.moderation.response.UserBlockListItemResponseDto;
import uz.consortgroup.forum_service.client.ModerationUserDirectoryClient;
import uz.consortgroup.forum_service.entity.ForumUserBlock;
import uz.consortgroup.forum_service.repository.ForumUserBlockRepository;
import uz.consortgroup.forum_service.service.ModerationQueryService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModerationQueryServiceImpl implements ModerationQueryService {

    private final ModerationUserDirectoryClient userClient;
    private final ForumUserBlockRepository blockRepo;

    @Transactional(readOnly = true)
    @Override
    public Page<UserBlockListItemResponseDto> listBlocks(UUID forumId, Pageable pageable) {
        log.info("Listing blocks: forumId={}, page={}, size={}, sort={}",
                forumId, pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        Page<ForumUserBlock> page = blockRepo.findByForumId(forumId, pageable);
        log.debug("Loaded {} block rows (totalElements={}, totalPages={})",
                page.getNumberOfElements(), page.getTotalElements(), page.getTotalPages());

        Set<UUID> ids = new HashSet<>();
        page.forEach(b -> { ids.add(b.getUserId()); ids.add(b.getIssuedBy()); });
        log.debug("Collected {} unique user ids for enrichment", ids.size());

        Map<UUID, ModerationUserInfoResponseDto> users = ids.isEmpty()
                ? Map.of()
                : userClient.getModerationUsers(new ArrayList<>(ids));

        log.debug("Enriched users map size={}", users.size());

        Page<UserBlockListItemResponseDto> mapped = page.map(b -> UserBlockListItemResponseDto.builder()
                .id(b.getId())
                .forumId(b.getForumId())
                .user(users.get(b.getUserId()))
                .issuedBy(users.get(b.getIssuedBy()))
                .reason(b.getReason())
                .createdAt(b.getCreatedAt())
                .violationCount(b.getViolationCount())
                .build());

        log.info("Returning {} items for forumId={} (page {}/{})",
                mapped.getNumberOfElements(), forumId, mapped.getNumber() + 1, mapped.getTotalPages());
        return mapped;
    }
}
