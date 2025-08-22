package uz.consortgroup.forum_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uz.consortgroup.core.api.v1.dto.forum.CreateForumTopicRequest;
import uz.consortgroup.core.api.v1.dto.forum.ForumTopicResponse;
import uz.consortgroup.forum_service.service.service.ForumTopicService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/forum/forum-topic")
@RequiredArgsConstructor
@Validated
@Tag(name = "Forum Topics", description = "Операции с темами форума")
public class ForumTopicController {

    private final ForumTopicService forumTopicService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(
            value = "/{forumId}/topics",
            consumes = "application/json",
            produces = "application/json"
    )
    @Operation(
            summary = "Создать тему",
            description = "Создаёт новую тему в указанном форуме.",
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponse(
            responseCode = "201",
            description = "Тема успешно создана",
            content = @Content(
                    schema = @Schema(implementation = ForumTopicResponse.class),
                    examples = @ExampleObject(
                            name = "Успешный ответ",
                            value = """
                                    {
                                      "id": "5c6f2b36-2e3c-4c5f-9d9e-8f0c1f2a3b4c",
                                      "forumId": "8b9f1f0d-7a71-4f5e-8b5a-1234567890ab",
                                      "title": "Java основы",
                                      "content": "Разбираем базовые принципы и практики",
                                      "authorId": "2a1f0e9d-1234-4bcd-9abc-abcdef012345",
                                      "createdAt": "2025-08-22T09:30:00Z"
                                    }
                                    """
                    )
            )
    )
    @ApiResponse(responseCode = "400", description = "Некорректный запрос (валидация)", content = @Content)
    @ApiResponse(responseCode = "401", description = "Неавторизован", content = @Content)
    @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content)
    @ApiResponse(responseCode = "404", description = "Форум не найден", content = @Content)
    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    public ForumTopicResponse createForumTopic(
            @Parameter(
                    in = ParameterIn.PATH,
                    required = true,
                    description = "Идентификатор форума (UUID)",
                    example = "8b9f1f0d-7a71-4f5e-8b5a-1234567890ab"
            )
            @PathVariable UUID forumId,

            @RequestBody(
                    required = true,
                    description = "Данные новой темы",
                    content = @Content(
                            schema = @Schema(implementation = CreateForumTopicRequest.class),
                            examples = @ExampleObject(
                                    name = "Пример запроса",
                                    value = """
                                            {
                                              "title": "Как настроить Spring Security?",
                                              "content": "Поделитесь конфигом для JWT и фильтров."
                                            }
                                            """
                            )
                    )
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody CreateForumTopicRequest request
    ) {
        return forumTopicService.createForumTopic(forumId, request);
    }
}
