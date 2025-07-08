package io.petprojects.bookshelfs.service.mapper;

import io.petprojects.bookshelfs.entity.BookShareEntity;
import io.petprojects.bookshelfs.model.response.BookShareNotificationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface BookShareMapper {
    List<BookShareNotificationResponse> toListInfoResponse(List<BookShareEntity> bookShareEntity);

    @Mapping(target = "bookTitle", expression = "java(bookShareEntity.getBook().getTitle())")
    @Mapping(target = "requesterName", expression = "java(bookShareEntity.getRequester().getPublicName())")
    @Mapping(target = "ownerName", expression = "java(bookShareEntity.getOwner().getPublicName())")
    @Mapping(target = "shareStatus", expression = "java(bookShareEntity.getShareStatus().title)")
    @Mapping(target = "accessType", expression = "java(bookShareEntity.getAccessType().title)")
    BookShareNotificationResponse toInfoResponse(BookShareEntity bookShareEntity);
}
