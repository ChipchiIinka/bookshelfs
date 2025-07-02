package io.petprojects.bookshelfs.conroller;

import io.petprojects.bookshelfs.exception.baseresponse.BaseResponseService;
import io.petprojects.bookshelfs.exception.baseresponse.ResponseWrapper;
import io.petprojects.bookshelfs.model.ReaderResponse;
import io.petprojects.bookshelfs.service.ReaderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/readers")
@RequiredArgsConstructor
public class ReaderController {

    private final ReaderService readerService;
    private final BaseResponseService baseResponseService;

    @Operation(summary = "Получить список всех пользователей",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @GetMapping
    public ResponseWrapper<List<ReaderResponse>> getReaders() {
        return baseResponseService.wrapSuccessResponse(readerService.findAllReaders());
    }

    @Operation(summary = "Получить данные пользователя по ID",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @GetMapping("/{readerId}")
    public ResponseWrapper<ReaderResponse> getReaderById(@PathVariable long readerId) {
        return baseResponseService.wrapSuccessResponse(readerService.findReaderById(readerId));
    }
}
