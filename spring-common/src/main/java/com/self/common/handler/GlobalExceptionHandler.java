package com.self.common.handler;

import com.self.common.exception.*;
import com.self.common.domain.ResultEntity;
import com.self.common.enums.RespCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * 全局异常处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ParamException.class)
    public final ResultEntity<Object> handleParamExceptions(Exception ex) {
        return ResultEntity.addError(RespCodeEnum.FAIL_PARAM.getCode(), ex.getMessage());
    }

    @ExceptionHandler(UnAuthorizedException.class)
    public final ResultEntity<Object> handleAuthExceptions(Exception ex) {
        return ResultEntity.addError(RespCodeEnum.FAIL_UNAUTHORIZED.getCode(), ex.getMessage());
    }

    @ExceptionHandler(BizException.class)
    public final ResultEntity<Object> handleBizExceptions(Exception ex) {
        return ResultEntity.addError(RespCodeEnum.FAIL_BIZ.getCode(), ex.getMessage());
    }

    @ExceptionHandler(RateLimiterException.class)
    public final ResultEntity<Object> handleRateLimiterExceptions(Exception ex) {
        return ResultEntity.addError(RespCodeEnum.FAIL_TOO_MANY_REQUESTS.getCode(), ex.getMessage());
    }

    @ExceptionHandler(HttpException.class)
    public final ResultEntity<Object> handleHttpExceptions(Exception ex) {
        logger.error(ex.getMessage(), ex);
        return ResultEntity.addError(RespCodeEnum.FAIL_SYS.getCode(), ex.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public final ResultEntity<Object> handleMaxUploadSizeExceededExceptions(Exception ex) {
        return ResultEntity.addError(RespCodeEnum.FAIL_SYS.getCode(), "上传文件不能超过限制大小");
    }

    @ExceptionHandler(Exception.class)
    public final ResultEntity<Object> handleAllExceptions(Exception ex) {
        logger.error(ex.getMessage(), ex);
        return ResultEntity.addError(RespCodeEnum.FAIL_SYS.getCode(), "系统错误，请联系管理员");
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public final ResultEntity<Object> handleInternalAuthExceptions(Exception ex) {
        return ResultEntity.addError(RespCodeEnum.FAIL_UNAUTHORIZED.getCode(), ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public final ResultEntity<Object> handleBadCredentialsExceptions(Exception ex) {
        return ResultEntity.addError(RespCodeEnum.FAIL_UNAUTHORIZED.getCode(), "用户名密码错误");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public final ResultEntity<Object> handleAccessDeniedExceptions(Exception ex) {
        return ResultEntity.addError(RespCodeEnum.FAIL_UNAUTHORIZED.getCode(), "权限不足");
    }

    /**
     * 参数校验异常
     */
    @ExceptionHandler(BindException.class)
    public final ResultEntity<Object> validateBindExceptions(BindException e) {
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return ResultEntity.addError(RespCodeEnum.FAIL_PARAM.getCode(), message);
    }

    /**
     * json参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResultEntity<Object> validateBindExceptions(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResultEntity.addError(RespCodeEnum.FAIL_PARAM.getCode(), message);
    }

}
