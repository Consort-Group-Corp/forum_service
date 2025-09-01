package uz.consortgroup.forum_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uz.consortgroup.core.api.v1.dto.forum.ForumCommentsCountResponse;
import uz.consortgroup.forum_service.handler.ErrorResponse;
import uz.consortgroup.forum_service.service.service.ForumCommentService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/forums")
@RequiredArgsConstructor
@Tag(name = "Forum statistics", description = "Операции по статистике форума")
public class ForumStatisticsController {

    private final ForumCommentService forumCommentService;

    @GetMapping(value = "/comments/total", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Общее количество комментариев во всех форумах",
            description = "Возвращает суммарное число комментариев по всем форумам, включая приватные."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Успешный ответ",
            content = @Content(
                    schema = @Schema(implementation = ForumCommentsCountResponse.class),
                    examples = @ExampleObject(
                            name = "Пример",
                            value = "{\n  \"totalCount\": 12000\n}"
                    )
            )
    )
    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ForumCommentsCountResponse totalComments() {
        return new ForumCommentsCountResponse(forumCommentService.getTotalCommentsCount());
    }

    @GetMapping(value = "/{forumId}/comments/total", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Количество комментариев в конкретном форуме",
            description = "Возвращает число комментариев для указанного форума (включая приватные)."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Успешный ответ",
            content = @Content(
                    schema = @Schema(implementation = ForumCommentsCountResponse.class),
                    examples = @ExampleObject(
                            name = "Пример",
                            value = "{\n  \"totalCount\": 345\n}"
                    )
            )
    )
    @ApiResponse(responseCode = "400", description = "Некорректный идентификатор форума",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Форум не найден",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ForumCommentsCountResponse commentsByForum(
            @Parameter(
                    in = ParameterIn.PATH,
                    required = true,
                    description = "UUID форума",
                    example = "8b9f1f0d-7a71-4f5e-8b5a-1234567890ab"
            )
            @PathVariable UUID forumId
    ) {
        return new ForumCommentsCountResponse(forumCommentService.getCommentsCountByForumId(forumId));
    }
}
