package com.nakamas.hatfieldbackend.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {
    ADMIN("ADMIN"), ENGINEER("WORKER"), SALESMAN("WORKER"), CLIENT("CLIENT");

    private final String role;
    public static final String ADMIN_VALUE = "ADMIN";
    public static final String WORKER_VALUE = "WORKER";
    public static final String CLIENT_VALUE = "CLIENT";
}
