package com.self.biz.handler;

import com.self.biz.exception.BizException;
import com.self.biz.exception.ParamException;
import com.self.biz.exception.UnAuthorizedException;
import com.self.common.domain.ResultEntity;
import com.self.common.enums.RespCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
    public final ResultEntity<Object> handleBizExceptions(BizException ex) {
        return ResultEntity.addError(RespCodeEnum.FAIL_BIZ.getCode(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public final ResultEntity<Object> handleAllExceptions(Exception ex) {
        logger.error(ex.getMessage(), ex);
        return ResultEntity.addError(RespCodeEnum.FAIL_SYS.getCode(), "系统错误，请联系管理员");
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
