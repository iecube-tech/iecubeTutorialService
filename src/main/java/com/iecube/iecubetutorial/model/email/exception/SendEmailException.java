package com.iecube.iecubetutorial.model.email.exception;

import com.iecube.iecubetutorial.exception.ServiceException;

public class SendEmailException extends ServiceException {
    public SendEmailException() {
        super();
    }

    public SendEmailException(String message) {
        super(message);
    }

    public SendEmailException(String message, Throwable cause) {
        super(message, cause);
    }

    public SendEmailException(Throwable cause) {
        super(cause);
    }

    protected SendEmailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
