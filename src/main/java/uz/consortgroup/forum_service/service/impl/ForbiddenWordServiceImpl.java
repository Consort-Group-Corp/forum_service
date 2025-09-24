package uz.consortgroup.forum_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.forum.request.ForbiddenWordCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.request.ForbiddenWordUpdateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.response.ForbiddenWordCheckResponseDto;
import uz.consortgroup.core.api.v1.dto.forum.response.ForbiddenWordResponseDto;
import uz.consortgroup.forum_service.entity.ForumForbiddenWord;
import uz.consortgroup.forum_service.mapper.ForbiddenWordMapper;
import uz.consortgroup.forum_service.repository.ForumForbiddenWordRepository;
import uz.consortgroup.forum_service.security.AuthContext;
import uz.consortgroup.forum_service.service.ForbiddenWordService;
import uz.consortgroup.forum_service.validator.ForbiddenWordValidator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ForbiddenWordServiceImpl implements ForbiddenWordService {

    private final ForumForbiddenWordRepository repository;
    private final ForbiddenWordMapper mapper;
    private final ForbiddenWordValidator validator;
    private final AuthContext authContext;

    @Override
    @Transactional
    public ForbiddenWordResponseDto createForbiddenWord(ForbiddenWordCreateRequestDto dto) {
        UUID createdBy = authContext.getCurrentUserId();

        log.info("Creating forbidden word by user={}", createdBy);
        validator.validateCreate(dto);

        ForumForbiddenWord entity = mapper.toEntityOnCreate(dto);
        entity.setWord(validator.normalize(entity.getWord()));
        entity.setCreatedBy(createdBy);
        entity.setActive(true);
        entity.setCreatedAt(Instant.now());

        ForumForbiddenWord saved = repository.save(entity);
        ForbiddenWordResponseDto response = mapper.toDto(saved);
        log.info("Forbidden word created: id={}, word={}", saved.getId(), saved.getWord());
        return response;
    }

    @Override
    @Transactional
    public ForbiddenWordResponseDto updateForbiddenWord(UUID id, ForbiddenWordUpdateRequestDto dto) {
        log.info("Updating forbidden word id={}", id);
        ForumForbiddenWord target = validator.validateUpdateTarget(id);

        ForumForbiddenWord updated = mapper.updateEntityFromDto(dto, target);
        if (dto.getWord() != null) {
            updated.setWord(validator.normalize(dto.getWord()));
        }
        updated.setUpdatedAt(Instant.now());

        ForumForbiddenWord saved = repository.save(updated);
        ForbiddenWordResponseDto response = mapper.toDto(saved);
        log.info("Forbidden word updated: id={}, active={}, word={}", saved.getId(), saved.isActive(), saved.getWord());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ForbiddenWordResponseDto> getForbiddenWords(Pageable pageable, Boolean active) {
        Page<ForumForbiddenWord> page = (active == null)
                ? repository.findAll(pageable)
                : repository.findByActive(active, pageable);

        return page.map(mapper::toDto);
    }


    @Override
    @Transactional(readOnly = true)
    public ForbiddenWordCheckResponseDto checkText(String text) {
        String normalized = text == null ? "" : text.toLowerCase(Locale.ROOT);
        List<ForumForbiddenWord> all = repository.findAll();
        List<String> matched = new ArrayList<>();

        for (ForumForbiddenWord w : all) {
            if (!w.isActive()) continue;

            String token = w.getWord() == null ? "" : w.getWord().toLowerCase(Locale.ROOT).trim();

            if (token.isEmpty()) continue;

            if (normalized.contains(token)) {
                matched.add(w.getWord());
            }
        }

        boolean violated = !matched.isEmpty();
        return ForbiddenWordCheckResponseDto.builder()
                .violated(violated)
                .matched(matched)
                .build();
    }
}
