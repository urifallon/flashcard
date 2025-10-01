package com.flashcard.backend.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuditAction {
    LOGIN("LOGIN"),
    LOGOUT("LOGOUT"),
    CREATE("CREATE"),
    UPDATE("UPDATE"),
    DELETE("DELETE");

    private final String value;
}
