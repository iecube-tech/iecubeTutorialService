package com.iecube.iecubetutorial.model.resource.exception;

import com.iecube.iecubetutorial.exception.ServiceException;

public class HandelFileFailedException extends ServiceException {
    public HandelFileFailedException() {
        super();
    }

    public HandelFileFailedException(String message) {
        super(message);
    }

    public HandelFileFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public HandelFileFailedException(Throwable cause) {
        super(cause);
    }

    protected HandelFileFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
