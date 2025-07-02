package io.petprojects.bookshelfs.service.mapper;

import io.petprojects.bookshelfs.entity.ReaderEntity;
import io.petprojects.bookshelfs.model.ReaderResponse;
import io.petprojects.bookshelfs.model.RegisterRequest;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ReaderMapper {
    List<ReaderResponse> toResponseList(List<ReaderEntity> readerEntityList);
    ReaderResponse toResponse(ReaderEntity readerEntity);
    ReaderEntity toEntity(RegisterRequest registerRequest, String verificationCode,
                          LocalDateTime verificationCodeExpiry, Boolean enabled);
}
