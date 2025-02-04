package com.github.ivanas93.exception;

import java.io.Serial;

public class EntropySourceException extends PasswordGenerationException {
    @Serial
    private static final long serialVersionUID = 8216854485931903107L;

    public EntropySourceException(String message) {
        super(message);
    }

    public EntropySourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
