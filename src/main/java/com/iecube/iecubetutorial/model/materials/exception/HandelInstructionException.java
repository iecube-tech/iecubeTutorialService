package com.iecube.iecubetutorial.model.materials.exception;

import com.iecube.iecubetutorial.exception.ServiceException;

public class HandelInstructionException extends ServiceException {
    public HandelInstructionException() {
        super();
    }

    public HandelInstructionException(String message) {
        super(message);
    }

    public HandelInstructionException(String message, Throwable cause) {
        super(message, cause);
    }

    public HandelInstructionException(Throwable cause) {
        super(cause);
    }

    protected HandelInstructionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
