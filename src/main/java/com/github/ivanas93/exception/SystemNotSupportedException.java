package com.github.ivanas93.exception;

import java.io.Serial;

public class SystemNotSupportedException extends PasswordGenerationException {
    @Serial
    private static final long serialVersionUID = -5814286146210277983L;

    public SystemNotSupportedException(String message) {
        super(message);
    }
}
