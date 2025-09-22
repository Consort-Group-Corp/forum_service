package uz.consortgroup.forum_service.strategy;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uz.consortgroup.core.api.v1.dto.forum.enumeration.ForumAccessPolicy;
import uz.consortgroup.forum_service.exception.ForumValidationException;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ForumAccessStrategyResolver {

    private final List<ForumAccessStrategy> strategies;
    private final Map<ForumAccessPolicy, ForumAccessStrategy> registry = new EnumMap<>(ForumAccessPolicy.class);

    @PostConstruct
    void init() {
        for (ForumAccessStrategy s : strategies) {
            registry.put(s.supports(), s);
        }
    }

    public ForumAccessStrategy resolve(ForumAccessPolicy policy) {
        ForumAccessStrategy strategy = registry.get(policy);
        if (strategy == null) {
            throw new ForumValidationException("Unsupported access policy: " + policy);
        }
        return strategy;
    }
}
