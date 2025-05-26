package com.iecube.iecubetutorial.exception;

public class HandelFileException extends ServiceException{
    public HandelFileException() {
        super();
    }

    public HandelFileException(String message) {
        super(message);
    }

    public HandelFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public HandelFileException(Throwable cause) {
        super(cause);
    }

    protected HandelFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
