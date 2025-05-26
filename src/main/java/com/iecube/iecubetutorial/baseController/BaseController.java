package com.iecube.iecubetutorial.baseController;

import com.iecube.iecubetutorial.exception.ServiceException;
import com.iecube.iecubetutorial.model.user.exception.AuthException;
import com.iecube.iecubetutorial.util.JsonResult;
import com.iecube.iecubetutorial.util.exception.SystemException;
import com.iecube.iecubetutorial.util.jwt.AuthUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class BaseController {
    public static final int OK=200;

    public static final int BAD_REQUEST=400;

    public final Long currentUserId(){
        return AuthUtils.getCurrentUserId();
    }

    public JsonResult<String> handleParamsError(String param){
        return new JsonResult<>(BAD_REQUEST, param);
    }

    @ExceptionHandler(ServiceException.class)
    public JsonResult<Void> handleException(Throwable e){
        JsonResult<Void> result = new JsonResult<>(e);

        if(e instanceof SystemException) {
            result.setState(404);
            result.setMessage("文件未找到");
        } else if (e instanceof AuthException) {
            result.setState(401);
            result.setMessage(e.getMessage());
        }
        return result;
    }
}
