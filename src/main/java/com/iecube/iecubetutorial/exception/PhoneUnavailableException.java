package com.iecube.iecubetutorial.exception;

public class PhoneUnavailableException extends ServiceException {
    public PhoneUnavailableException() {
        super();
    }

    public PhoneUnavailableException(String message) {
        super(message);
    }

    public PhoneUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public PhoneUnavailableException(Throwable cause) {
        super(cause);
    }

    protected PhoneUnavailableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
