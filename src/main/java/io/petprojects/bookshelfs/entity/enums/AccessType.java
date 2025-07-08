package io.petprojects.bookshelfs.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccessType {
    TEMPORARY("временно"),
    PERMANENT("навсегда");

    public final String title;
}
