package com.iecube.iecubetutorial.model.invitationCode.exception;

import com.iecube.iecubetutorial.exception.ServiceException;

public class CodeExistOrUsedException extends ServiceException {
    public CodeExistOrUsedException() {
        super();
    }

    public CodeExistOrUsedException(String message) {
        super(message);
    }

    public CodeExistOrUsedException(String message, Throwable cause) {
        super(message, cause);
    }

    public CodeExistOrUsedException(Throwable cause) {
        super(cause);
    }

    protected CodeExistOrUsedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
