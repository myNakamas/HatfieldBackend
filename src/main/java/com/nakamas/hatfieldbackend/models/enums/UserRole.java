package com.nakamas.hatfieldbackend.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {
    ADMIN("ADMIN"), ENGINEER("WORKER"), SALESMAN("WORKER"), CLIENT("CLIENT");

    private final String role;
}
