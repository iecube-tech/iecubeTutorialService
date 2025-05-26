package com.iecube.iecubetutorial.model.materials.exception;

import com.iecube.iecubetutorial.exception.ServiceException;

public class FailedToCreateTaskException extends ServiceException {
    public FailedToCreateTaskException() {
        super();
    }

    public FailedToCreateTaskException(String message) {
        super(message);
    }

    public FailedToCreateTaskException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedToCreateTaskException(Throwable cause) {
        super(cause);
    }

    protected FailedToCreateTaskException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
