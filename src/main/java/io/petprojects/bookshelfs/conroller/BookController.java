package io.petprojects.bookshelfs.conroller;

import io.petprojects.bookshelfs.aop.CheckUserPermission;
import io.petprojects.bookshelfs.exception.baseresponse.BaseResponseService;
import io.petprojects.bookshelfs.exception.baseresponse.ResponseWrapper;
import io.petprojects.bookshelfs.model.request.BookUpdateRequest;
import io.petprojects.bookshelfs.model.response.BookContentResponse;
import io.petprojects.bookshelfs.model.response.BookInfoResponse;
import io.petprojects.bookshelfs.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/readers/{readerId}/bookshelfs/{bookshelfId}/books")
@RequiredArgsConstructor
@Tag(name = "Книги", description = "Методы для работы с книгами")
public class BookController {
    private final BookService bookService;
    private final BaseResponseService baseResponseService;

    @Operation(summary = "Загрузить книгу",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @CheckUserPermission
    public ResponseWrapper<?> uploadBook(
            @PathVariable Long readerId,
            @PathVariable Long bookshelfId,
            @RequestPart(value = "file") MultipartFile file,
            @RequestParam String description) throws IOException {
        return baseResponseService.wrapSuccessResponse(bookService.upload(readerId, bookshelfId, file, description));
    }

    @Operation(summary = "Получить подробную информацию о книге",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @GetMapping("/{bookId}")
    public ResponseWrapper<BookInfoResponse> getBookById(@PathVariable Long bookId) {
        return baseResponseService.wrapSuccessResponse(bookService.findById(bookId));
    }

    @Operation(summary = "Читать книгу",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @GetMapping("/{bookId}/read")
    @CheckUserPermission
    public ResponseWrapper<BookContentResponse> readBook(
            @PathVariable Long bookId,
            @PathVariable Long readerId) {
        return baseResponseService.wrapSuccessResponse(bookService.readBook(bookId, readerId));
    }

    @Operation(summary = "Обновление прогресса чтения книги",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @PostMapping("/{bookId}/read")
    @CheckUserPermission
    public ResponseWrapper<?> updateProgress(
            @PathVariable Long readerId,
            @PathVariable Long bookId,
            @RequestParam int lastReadPage) {
        return baseResponseService.wrapSuccessResponse(
                bookService.updateProgress(readerId, bookId, lastReadPage));
    }

    @Operation(summary = "Изменить данные книги",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @PatchMapping("/{bookId}")
    @CheckUserPermission
    public ResponseWrapper<?> updateBook(
            @PathVariable Long readerId,
            @PathVariable Long bookId,
            @RequestBody @Valid BookUpdateRequest request) {
        return baseResponseService.wrapSuccessResponse(bookService.update(bookId, request));
    }

    @Operation(summary = "Удалить книгу",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @DeleteMapping("/{bookId}")
    @CheckUserPermission
    public ResponseWrapper<?> deleteBook(
            @PathVariable Long readerId,
            @PathVariable Long bookId) {
        return baseResponseService.wrapSuccessResponse(bookService.delete(bookId));
    }
}
