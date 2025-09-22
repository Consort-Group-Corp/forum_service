package uz.consortgroup.forum_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uz.consortgroup.forum_service.exception.ForumValidationException;
import uz.consortgroup.forum_service.service.ForbiddenWordService;

@Component
@RequiredArgsConstructor
public class ContentPolicyChecker {

    private final ForbiddenWordService forbiddenWordService;

    public void ensureNoForbidden(String... texts) {
        if (texts == null) return;
        for (String t : texts) {
            if (t == null || t.isBlank()) continue;
            if (forbiddenWordService.checkText(t).isViolated()) {
                throw new ForumValidationException("Content contains forbidden words");
            }
        }
    }
}
