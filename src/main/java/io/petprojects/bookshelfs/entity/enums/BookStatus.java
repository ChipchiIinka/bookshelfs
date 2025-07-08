package io.petprojects.bookshelfs.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BookStatus {
    PENDING("В процессе преобразования"),
    CONVERTED("Успешно преобразована"),
    FAILED("Преобразование не удалось");

    public final String title;
}
