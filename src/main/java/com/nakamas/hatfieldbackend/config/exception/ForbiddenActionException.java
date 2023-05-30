package com.nakamas.hatfieldbackend.config.exception;

import lombok.Getter;

@Getter
public class ForbiddenActionException extends RuntimeException {

    public ForbiddenActionException(String message) {
        super(message);
    }
}
