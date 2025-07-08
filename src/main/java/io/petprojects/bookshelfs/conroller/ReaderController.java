package io.petprojects.bookshelfs.conroller;

import io.petprojects.bookshelfs.aop.CheckUserPermission;
import io.petprojects.bookshelfs.exception.baseresponse.BaseResponseService;
import io.petprojects.bookshelfs.exception.baseresponse.ResponseWrapper;
import io.petprojects.bookshelfs.model.request.ReaderUpdateRequest;
import io.petprojects.bookshelfs.model.response.ReaderInfoResponse;
import io.petprojects.bookshelfs.model.response.ReaderListResponse;
import io.petprojects.bookshelfs.service.ReaderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/readers")
@RequiredArgsConstructor
@Tag(name = "Читатели", description = "Методы для работы с читателями")
public class ReaderController {
    private final ReaderService readerService;
    private final BaseResponseService baseResponseService;

    @Operation(summary = "Получить список всех пользователей",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @GetMapping
    public ResponseWrapper<List<ReaderListResponse>> getAllReaders() {
        return baseResponseService.wrapSuccessResponse(readerService.findAll());
    }

    @Operation(summary = "Получить данные пользователя",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @GetMapping("/{readerId}/bookshelfs")
    public ResponseWrapper<ReaderInfoResponse> getReader(@PathVariable Long readerId) {
        return baseResponseService.wrapSuccessResponse(readerService.findById(readerId));
    }

    @Operation(summary = "Обновить данные этого пользователя",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @PutMapping("/{readerId}/bookshelfs")
    @CheckUserPermission
    public ResponseWrapper<?> updateReader(
            @PathVariable Long readerId,
            @RequestBody @Valid ReaderUpdateRequest readerUpdateRequest) {
        return baseResponseService.wrapSuccessResponse(
                readerService.updateById(readerId, readerUpdateRequest));
    }
}