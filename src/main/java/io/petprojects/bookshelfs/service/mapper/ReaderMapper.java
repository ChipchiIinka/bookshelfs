package io.petprojects.bookshelfs.service.mapper;

import io.petprojects.bookshelfs.entity.ReaderEntity;
import io.petprojects.bookshelfs.model.request.RegisterRequest;
import io.petprojects.bookshelfs.model.response.BookshelfListResponse;
import io.petprojects.bookshelfs.model.response.ReaderInfoResponse;
import io.petprojects.bookshelfs.model.response.ReaderListResponse;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ReaderMapper {
    List<ReaderListResponse> toListResponse(List<ReaderEntity> readerEntityList);
    ReaderListResponse toReaderResponse(ReaderEntity readerEntity);

    ReaderInfoResponse toInfoResponse(ReaderEntity readerEntity, List<BookshelfListResponse> bookshelfs);

    ReaderEntity toEntity(RegisterRequest registerRequest, String verificationCode,
                          LocalDateTime verificationCodeExpiry, Boolean enabled);
}
