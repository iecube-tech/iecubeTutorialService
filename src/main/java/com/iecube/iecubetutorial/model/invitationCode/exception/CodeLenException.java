package com.iecube.iecubetutorial.model.invitationCode.exception;

import com.iecube.iecubetutorial.exception.ServiceException;

public class CodeLenException extends ServiceException {
    public CodeLenException() {
        super();
    }

    public CodeLenException(String message) {
        super(message);
    }

    public CodeLenException(String message, Throwable cause) {
        super(message, cause);
    }

    public CodeLenException(Throwable cause) {
        super(cause);
    }

    protected CodeLenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
