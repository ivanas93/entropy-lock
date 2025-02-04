package com.github.ivanas93.exception;

import java.io.Serial;

public class HashingException extends PasswordGenerationException {
    @Serial
    private static final long serialVersionUID = 2588150544895458551L;

    public HashingException(String message) {
        super(message);
    }

    public HashingException(String message, Throwable cause) {
        super(message, cause);
    }
}
