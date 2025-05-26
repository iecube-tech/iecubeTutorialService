package com.iecube.iecubetutorial.model.sms.exception;

import com.iecube.iecubetutorial.exception.ServiceException;

public class SendSMSMessageException extends ServiceException {
    public SendSMSMessageException() {
        super();
    }

    public SendSMSMessageException(String message) {
        super(message);
    }

    public SendSMSMessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public SendSMSMessageException(Throwable cause) {
        super(cause);
    }

    protected SendSMSMessageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
