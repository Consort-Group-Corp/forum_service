package uz.consortgroup.forum_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uz.consortgroup.core.api.v1.dto.forum.CreateForumCommentRequest;
import uz.consortgroup.core.api.v1.dto.forum.ForumCommentResponse;
import uz.consortgroup.forum_service.service.service.ForumCommentService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/forum/forum-comment")
@RequiredArgsConstructor
@Validated
@Tag(name = "Forum Comments", description = "Операции с комментариями в топиках форума")
public class ForumCommentController {

    private final ForumCommentService forumCommentService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(
            value = "/{topicId}/comments",
            consumes = "application/json",
            produces = "application/json"
    )
    @Operation(
            summary = "Создать комментарий",
            description = "Создаёт новый комментарий в указанном топике форума.",
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponse(
            responseCode = "201",
            description = "Комментарий успешно создан",
            content = @Content(
                    schema = @Schema(implementation = ForumCommentResponse.class),
                    examples = @ExampleObject(name = "Успешный ответ", value =
                            "{\n" +
                                    "  \"id\": \"5c6f2b36-2e3c-4c5f-9d9e-8f0c1f2a3b4c\",\n" +
                                    "  \"topicId\": \"8b9f1f0d-7a71-4f5e-8b5a-1234567890ab\",\n" +
                                    "  \"authorId\": \"2a1f0e9d-1234-4bcd-9abc-abcdef012345\",\n" +
                                    "  \"content\": \"Спасибо за пояснение!\",\n" +
                                    "  \"createdAt\": \"2025-08-22T10:15:30Z\",\n" +
                                    "  \"updatedAt\": null\n" +
                                    "}"
                    )
            )
    )
    @ApiResponse(responseCode = "400", description = "Некорректный запрос (валидация)", content = @Content)
    @ApiResponse(responseCode = "401", description = "Неавторизован", content = @Content)
    @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content)
    @ApiResponse(responseCode = "404", description = "Топик не найден", content = @Content)
    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    public ForumCommentResponse createComment(
            @Parameter(
                    in = ParameterIn.PATH,
                    required = true,
                    description = "Идентификатор топика (UUID)",
                    example = "8b9f1f0d-7a71-4f5e-8b5a-1234567890ab"
            )
            @PathVariable UUID topicId,

            @RequestBody(
                    required = true,
                    description = "Данные для создания комментария",
                    content = @Content(
                            schema = @Schema(implementation = CreateForumCommentRequest.class),
                            examples = @ExampleObject(name = "Пример запроса", value =
                                    "{\n" +
                                            "  \"content\": \"Спасибо за пояснение!\"\n" +
                                            "}"
                            )
                    )
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody CreateForumCommentRequest request
    ) {
        return forumCommentService.createComment(topicId, request);
    }
}
