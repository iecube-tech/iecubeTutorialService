package com.iecube.iecubetutorial.model.invitationCode.exception;

import com.iecube.iecubetutorial.exception.ServiceException;

public class InvalidCodeException extends ServiceException {
    public InvalidCodeException() {
        super();
    }

    public InvalidCodeException(String message) {
        super(message);
    }

    public InvalidCodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidCodeException(Throwable cause) {
        super(cause);
    }

    protected InvalidCodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
