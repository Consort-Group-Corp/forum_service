package uz.consortgroup.forum_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uz.consortgroup.core.api.v1.dto.forum.ForumListParamsRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.ForumListResponseDto;
import uz.consortgroup.forum_service.service.service.ForumListQueryService;

@RestController
@RequestMapping("/api/v1/forums")
@RequiredArgsConstructor
@Validated
@Tag(name = "Forums", description = "Поиск и листинг форумов")
public class ForumQueryController {

    private final ForumListQueryService forumListQueryService;

    @GetMapping(produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Получить список форумов",
            description = """
                Возвращает страницы форумов с фильтрами:
                поиск по названию, фильтр доступа (OPEN/CLOSED/ALL), фильтрация по ментору,
                сортировка по дате создания, количеству комментариев/лайков и пагинация.
                """,
            security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponse(
            responseCode = "200",
            description = "Список успешно получен",
            content = @Content(
                    schema = @Schema(implementation = ForumListResponseDto.class),
                    examples = @ExampleObject(
                            name = "Пример ответа",
                            value = """
                            {
                              "total": 42,
                              "page": 1,
                              "limit": 10,
                              "data": [
                                {
                                  "id": "3f2c2a88-2a47-4d8c-9a69-0f92f0a1d234",
                                  "title": "Курс по Java",
                                  "author": {
                                    "id": "e8b1b0b2-9b2a-4e3b-8f75-8f9e8c7f6a11",
                                    "lastName": "Иванов",
                                    "firstName": "Иван",
                                    "middleName": "Иванович",
                                    "role": "MENTOR"
                                  },
                                  "createdAt": "2025-08-22T09:30:00Z",
                                  "commentsCount": 5,
                                  "likesCount": 12,
                                  "accessType": "OPEN",
                                  "previewImageUrl": "https://cdn.example.com/covers/java.png"
                                }
                              ]
                            }
                            """
                    )
            )
    )
    @ApiResponse(responseCode = "400", description = "Некорректные query-параметры (валидация)", content = @Content)
    @ApiResponse(responseCode = "401", description = "Неавторизован", content = @Content)
    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    public ForumListResponseDto list(
            @ParameterObject @Valid ForumListParamsRequestDto params
    ) {
        return forumListQueryService.list(params);
    }
}
