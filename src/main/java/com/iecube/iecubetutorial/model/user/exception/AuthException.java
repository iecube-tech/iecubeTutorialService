package com.iecube.iecubetutorial.model.user.exception;

import com.iecube.iecubetutorial.exception.ServiceException;

public class AuthException extends ServiceException {
    public AuthException() {
        super();
    }

    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthException(Throwable cause) {
        super(cause);
    }

    protected AuthException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
