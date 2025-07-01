package com.iecube.iecubetutorial.model_user.points.exception;

import com.iecube.iecubetutorial.exception.ServiceException;

public class PointsNotEnoughException extends ServiceException {
    public PointsNotEnoughException() {
        super();
    }

    public PointsNotEnoughException(String message) {
        super(message);
    }

    public PointsNotEnoughException(String message, Throwable cause) {
        super(message, cause);
    }

    public PointsNotEnoughException(Throwable cause) {
        super(cause);
    }

    protected PointsNotEnoughException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
