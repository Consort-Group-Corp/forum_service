package uz.consortgroup.forum_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uz.consortgroup.core.api.v1.dto.forum.request.ForbiddenWordCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.request.ForbiddenWordUpdateRequestDto;
import uz.consortgroup.core.api.v1.dto.forum.response.ForbiddenWordCheckResponseDto;
import uz.consortgroup.core.api.v1.dto.forum.response.ForbiddenWordResponseDto;
import uz.consortgroup.forum_service.service.ForbiddenWordService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/forum/forbidden-words")
@RequiredArgsConstructor
public class ForbiddenWordController {

    private final ForbiddenWordService service;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ForbiddenWordResponseDto create(@Valid @RequestBody ForbiddenWordCreateRequestDto dto) {
        return service.create(dto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    public ForbiddenWordResponseDto update(@PathVariable UUID id, @Valid @RequestBody ForbiddenWordUpdateRequestDto dto) {
        return service.update(id, dto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Page<ForbiddenWordResponseDto> list(Pageable pageable,
                                               @RequestParam(required = false) Boolean active) {
        return service.list(pageable, active);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/check")
    public ForbiddenWordCheckResponseDto check(@RequestParam String text) {
        return service.checkText(text);
    }
}
