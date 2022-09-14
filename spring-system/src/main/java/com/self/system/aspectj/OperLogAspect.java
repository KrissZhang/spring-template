package com.self.system.aspectj;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.google.common.collect.Maps;
import com.self.common.annotation.OperLog;
import com.self.common.enums.BusinessStatusEnum;
import com.self.common.enums.HttpMethodEnum;
import com.self.common.jwt.JWTInfo;
import com.self.common.manager.AsyncManager;
import com.self.common.utils.CurUserUtils;
import com.self.common.utils.IpUtils;
import com.self.common.utils.ServletUtils;
import com.self.common.utils.SpringUtils;
import com.self.dao.service.OperLogService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.*;

@Aspect
@Component
public class OperLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(OperLogAspect.class);

    private static final SimplePropertyPreFilter propertyPreFilter;

    static {
        propertyPreFilter = new SimplePropertyPreFilter();
        propertyPreFilter.getExcludes().add("password");
    }

    /**
     * 配置织入点 - 自定义注解的包路径
     */
    @Pointcut("@annotation(com.self.common.annotation.OperLog)")
    public void logPointCut() {
        //ignore
    }

    /**
     * 处理完请求后执行
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "logPointCut()", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Object jsonResult) {
        handleLog(joinPoint, null, jsonResult);
    }

    /**
     * 拦截异常操作
     * @param joinPoint 切点
     * @param e 异常
     */
    @AfterThrowing(value = "logPointCut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
        handleLog(joinPoint, e, null);
    }

    protected void handleLog(final JoinPoint joinPoint, final Exception e, Object jsonResult) {
        try {
            HttpServletRequest request = ServletUtils.getRequest();

            //获得注解
            OperLog controllerLog = getAnnotationOperLog(joinPoint);
            if (controllerLog == null) {
                return;
            }

            //获取当前的用户
            JWTInfo jwtInfo = CurUserUtils.curJWTInfo();

            com.self.dao.entity.OperLog operLog = new com.self.dao.entity.OperLog();

            operLog.setTerminalType(ServletUtils.getTerminalType());
            operLog.setStatus(BusinessStatusEnum.SUCCESS.getValue());

            //请求ip
            String ip = IpUtils.getIpAddr(request);
            operLog.setReqIp(ip);

            //返回参数
            if(controllerLog.isSaveResponseData()){
                operLog.setRespResult(JSON.toJSONString(jsonResult));
            }

            operLog.setReqUrl(request.getRequestURI());

            operLog.setOperatorId(jwtInfo.getUserId());
            operLog.setOperatorName(jwtInfo.getRealName());

            if (e != null) {
                operLog.setStatus(BusinessStatusEnum.FAIL.getValue());
                operLog.setErrorMsg(StringUtils.substring(e.getMessage(), 0, 2000));
            }

            //方法名称
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            operLog.setMethod(className + "." + methodName + "()");

            //请求方式
            operLog.setReqMethod(request.getMethod());

            //注解参数
            getControllerMethodDescription(controllerLog, joinPoint, operLog);

            //添加记录
            AsyncManager.getInstance().execute(operRecordSave(operLog));
        } catch (Exception ex) {
            logger.error("记录操作日志异常：", ex);
        }
    }

    /**
     * 获取 Controller层 注解中对方法的描述信息
     *
     * @param controllerLog 日志注解
     * @param operLog 操作日志
     */
    public void getControllerMethodDescription(OperLog controllerLog, JoinPoint joinPoint, com.self.dao.entity.OperLog operLog) {
        //标题
        operLog.setTitle(controllerLog.title());

        //功能类别
        operLog.setBusinessType(controllerLog.businessType().getValue());

        //保存请求参数
        if (controllerLog.isSaveRequestData()) {
            setRequestParam(joinPoint, operLog);
        }
    }

    /**
     * 获取请求的参数
     */
    private void setRequestParam(JoinPoint joinPoint, com.self.dao.entity.OperLog operLog) {
        String reqMethod = operLog.getReqMethod();

        if (HttpMethodEnum.PUT.name().equals(reqMethod) || HttpMethodEnum.POST.name().equals(reqMethod)) {
            String params = argsArrayToString(joinPoint.getArgs());
            operLog.setReqParam(StringUtils.substring(params, 0, 2000));
        } else {
            Map<String, String> paramMap = Maps.newHashMap();
            HttpServletRequest request = ServletUtils.getRequest();
            Enumeration<String> params = request.getParameterNames();

            while (params.hasMoreElements()) {
                String paramName = params.nextElement();
                String[] paramValues = request.getParameterValues(paramName);
                paramMap.put(paramName, paramValues.length == 1 ? paramValues[0] : paramValues[paramValues.length - 1]);
            }

            operLog.setReqParam(StringUtils.substring(paramMap.toString(), 0, 2000));
        }
    }

    /**
     * 参数拼装
     */
    private String argsArrayToString(Object[] paramsArray) {
        StringBuilder params = new StringBuilder();

        if (paramsArray != null && paramsArray.length > 0) {
            for (Object o : paramsArray) {
                if (Objects.nonNull(o) && !isFilterObject(o)) {
                    try {
                        params.append(JSON.toJSONString(o, propertyPreFilter)).append(" ");
                    } catch (Exception ignored) {
                        //ignore
                    }
                }
            }
        }

        return params.toString().trim();
    }

    /**
     * 判断是否是需要过滤的对象
     *
     * @param obj 对象信息
     * @return 需要过滤--true，不需要过滤--false
     */
    @SuppressWarnings("rawtypes")
    public boolean isFilterObject(final Object obj) {
        Class<?> clazz = obj.getClass();
        if (clazz.isArray()) {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection collection = (Collection) obj;
            for (Object value : collection) {
                return value instanceof MultipartFile;
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map map = (Map) obj;
            for (Object value : map.entrySet()) {
                Map.Entry entry = (Map.Entry) value;
                return entry.getValue() instanceof MultipartFile;
            }
        }

        return obj instanceof MultipartFile || obj instanceof HttpServletRequest || obj instanceof HttpServletResponse
                || obj instanceof BindingResult;
    }

    /**
     * 获取注解
     */
    private OperLog getAnnotationOperLog(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();

        if (method != null) {
            return method.getAnnotation(OperLog.class);
        }

        return null;
    }

    private TimerTask operRecordSave(final com.self.dao.entity.OperLog operLog) {
        return new TimerTask() {
            @Override
            public void run() {
                operLog.setOperateTime(new Date());
                SpringUtils.getBean(OperLogService.class).save(operLog);
            }
        };
    }

}
