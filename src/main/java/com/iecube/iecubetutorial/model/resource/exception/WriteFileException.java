package com.iecube.iecubetutorial.model.resource.exception;

import com.iecube.iecubetutorial.exception.ServiceException;

public class WriteFileException extends ServiceException {
    public WriteFileException() {
        super();
    }

    public WriteFileException(String message) {
        super(message);
    }

    public WriteFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public WriteFileException(Throwable cause) {
        super(cause);
    }

    protected WriteFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
