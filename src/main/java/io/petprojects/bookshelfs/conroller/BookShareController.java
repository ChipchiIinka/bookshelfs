package io.petprojects.bookshelfs.conroller;

import io.petprojects.bookshelfs.aop.CheckUserPermission;
import io.petprojects.bookshelfs.entity.ReaderEntity;
import io.petprojects.bookshelfs.exception.baseresponse.BaseResponseService;
import io.petprojects.bookshelfs.exception.baseresponse.ResponseWrapper;
import io.petprojects.bookshelfs.model.request.BookShareRequest;
import io.petprojects.bookshelfs.service.BookShareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/readers/{readerId}")
@RequiredArgsConstructor
@Tag(name = "Делиться книгами", description = "Методы для запросов на книги")
public class BookShareController {
    private final BookShareService bookShareService;
    private final BaseResponseService baseResponseService;

    @Operation(summary = "Отправить запрос на книгу",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @PostMapping("/bookshelfs/{bookshelfId}/books/{bookId}/request")
    public ResponseWrapper<?> createShareRequest(
            @PathVariable Long bookId,
            @RequestBody @Valid BookShareRequest bookShareRequest,
            Authentication authentication) {
        Long requesterId = ((ReaderEntity) authentication.getPrincipal()).getId();
        return baseResponseService.wrapSuccessResponse(
                bookShareService.createShareRequest(bookId, requesterId, bookShareRequest));
    }

    @Operation(summary = "Посмотреть все свои уведомления о запросах",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @GetMapping
    @CheckUserPermission
    public ResponseWrapper<?> getAllNotifications(@PathVariable Long readerId) {
        return baseResponseService.wrapSuccessResponse(
                bookShareService.getAll(readerId));
    }


    @Operation(summary = "Ответить на запрос",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @PostMapping("/notifications/{notificationId}")
    @CheckUserPermission
    public ResponseWrapper<?> handleShareRequest(
            @PathVariable Long readerId,
            @PathVariable Long notificationId,
            @RequestParam String status,
            @RequestParam(required = false) String rejectionReason) {
        return baseResponseService.wrapSuccessResponse(
                bookShareService.handleShareRequest(notificationId, status, rejectionReason));
    }
}
