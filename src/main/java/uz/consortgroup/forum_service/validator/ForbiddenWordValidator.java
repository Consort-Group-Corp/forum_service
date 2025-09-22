package uz.consortgroup.forum_service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uz.consortgroup.core.api.v1.dto.forum.request.ForbiddenWordCreateRequestDto;
import uz.consortgroup.forum_service.entity.ForumForbiddenWord;
import uz.consortgroup.forum_service.exception.ForumValidationException;
import uz.consortgroup.forum_service.repository.ForumForbiddenWordRepository;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ForbiddenWordValidator {

    private final ForumForbiddenWordRepository repository;

    public void validateCreate(ForbiddenWordCreateRequestDto dto) {
        String w = normalize(dto.getWord());
        if (w.isBlank()) {
            throw new ForumValidationException("Word must not be blank");
        }

        boolean exists = repository.existsByWordIgnoreCase(w);

        if (exists) {
            throw new ForumValidationException("Word already exists");
        }
    }

    public ForumForbiddenWord validateUpdateTarget(UUID id) {
        Optional<ForumForbiddenWord> opt = repository.findById(id);
        if (opt.isEmpty()) {
            throw new ForumValidationException("Forbidden word not found: " + id);
        }
        return opt.get();
    }

    public String normalize(String raw) {
        return raw == null ? "" : raw.trim();
    }
}
