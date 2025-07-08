package io.petprojects.bookshelfs.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BookShareStatus {
    PENDING("ожидает принятия решения"),
    APPROVED("принят"),
    REJECTED("отклонен");

    public final String title;
}
