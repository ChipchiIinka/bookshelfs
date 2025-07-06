package io.petprojects.bookshelfs.service.mapper;

import io.petprojects.bookshelfs.entity.BookEntity;
import io.petprojects.bookshelfs.model.response.BookInfoResponse;
import io.petprojects.bookshelfs.model.response.BookListResponse;
import io.petprojects.bookshelfs.model.response.BookReadResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Mapper
public interface BookMapper {
    List<BookListResponse> toListResponse(List<BookEntity> bookEntities);
    @Mapping(target = "bookOwner", expression = "java(bookEntity.getBookOwner().getPublicName())")
    BookListResponse toBookResponse(BookEntity bookEntity);

    @Mapping(target = "bookOwner", expression = "java(bookEntity.getBookOwner().getPublicName())")
    @Mapping(target = "bookshelf", expression = "java(bookEntity.getBookshelf().getTitle())")
    BookInfoResponse toInfoResponse(BookEntity bookEntity);

    BookReadResponse toReadResponse(BookEntity bookEntity);

    @Mapping(target = "title", expression = "java(file.getOriginalFilename())")
    @Mapping(target = "data", expression = "java(file.getBytes())")
    BookEntity toEntity(MultipartFile file) throws IOException;
}
