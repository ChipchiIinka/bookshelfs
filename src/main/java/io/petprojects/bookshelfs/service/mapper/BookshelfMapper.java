package io.petprojects.bookshelfs.service.mapper;

import io.petprojects.bookshelfs.entity.BookshelfEntity;
import io.petprojects.bookshelfs.model.response.BookListResponse;
import io.petprojects.bookshelfs.model.response.BookshelfInfoResponse;
import io.petprojects.bookshelfs.model.response.BookshelfListResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface BookshelfMapper {
    List<BookshelfListResponse> toListResponse(List<BookshelfEntity> bookshelfEntities);
    BookshelfListResponse toBookshelfResponse(BookshelfEntity bookshelfEntity);

    BookshelfInfoResponse toInfoResponse(BookshelfEntity bookshelfEntity, List<BookListResponse> booksResponse);
}
