package com.iecube.iecubetutorial.model.verificationCode.excepion;

import com.iecube.iecubetutorial.exception.ServiceException;

public class InvalidVerificationCode extends ServiceException {
    public InvalidVerificationCode() {
        super();
    }

    public InvalidVerificationCode(String message) {
        super(message);
    }

    public InvalidVerificationCode(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidVerificationCode(Throwable cause) {
        super(cause);
    }

    protected InvalidVerificationCode(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
