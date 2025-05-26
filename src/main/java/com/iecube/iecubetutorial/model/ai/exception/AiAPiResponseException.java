package com.iecube.iecubetutorial.model.ai.exception;

import com.iecube.iecubetutorial.exception.ServiceException;

public class AiAPiResponseException extends ServiceException {
    public AiAPiResponseException() {
        super();
    }

    public AiAPiResponseException(String message) {
        super(message);
    }

    public AiAPiResponseException(String message, Throwable cause) {
        super(message, cause);
    }

    public AiAPiResponseException(Throwable cause) {
        super(cause);
    }

    protected AiAPiResponseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
