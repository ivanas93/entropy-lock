package com.github.ivanas93.exception;

import java.io.Serial;

public class PasswordGenerationException extends Exception {
    @Serial
    private static final long serialVersionUID = -1023182393926703993L;

    public PasswordGenerationException(String message) {
        super(message);
    }

    public PasswordGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
