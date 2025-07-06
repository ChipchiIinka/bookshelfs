package io.petprojects.bookshelfs.conroller;

import io.petprojects.bookshelfs.aop.CheckUserPermission;
import io.petprojects.bookshelfs.exception.baseresponse.BaseResponseService;
import io.petprojects.bookshelfs.exception.baseresponse.ResponseWrapper;
import io.petprojects.bookshelfs.model.response.BookshelfInfoResponse;
import io.petprojects.bookshelfs.service.BookshelfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/readers/{readerId}/bookshelfs")
@RequiredArgsConstructor
@Tag(name = "Полки", description = "Методы для работы с полками")
public class BookshelfController {
    private final BookshelfService bookshelfService;
    private final BaseResponseService baseResponseService;

    @Operation(summary = "Создать новую полку",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @PostMapping
    @CheckUserPermission
    public ResponseWrapper<?> createBookshelf(@PathVariable Long readerId,
                                              @RequestParam String bookshelfTitle) {
        return baseResponseService.wrapSuccessResponse(
                bookshelfService.create(readerId, bookshelfTitle));
    }

    @Operation(summary = "Получить данные конкретной полки",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @GetMapping("/{bookshelfId}/books")
    public ResponseWrapper<BookshelfInfoResponse> getBookshelfById(@PathVariable Long bookshelfId) {
        return baseResponseService.wrapSuccessResponse(bookshelfService.findById(bookshelfId));
    }

    @Operation(summary = "Изменить название полки",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @PatchMapping("/{bookshelfId}/books")
    @CheckUserPermission
    public ResponseWrapper<?> updateBookshelfTitle(@PathVariable Long readerId, @PathVariable Long bookshelfId,
                                                   @RequestParam @Valid String bookshelfNewTitle) {
        return baseResponseService.wrapSuccessResponse(
                bookshelfService.updateTitle(bookshelfId, bookshelfNewTitle));
    }

    @Operation(summary = "Удалить полку",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @DeleteMapping("/{bookshelfId}/books")
    @CheckUserPermission
    public ResponseWrapper<?> deleteBookshelf(@PathVariable Long readerId, @PathVariable Long bookshelfId) {
        return baseResponseService.wrapSuccessResponse(
                bookshelfService.delete(bookshelfId));
    }
}
