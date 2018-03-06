package com.wiceflow.annotation.util;

import com.alibaba.fastjson.JSONObject;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @author BF
 * @date 2018/3/5
 */
@Aspect     // 声明这是一个切面类
@Order(1)   // 设置切面优先级:如果多个切面,可通过优先级控制斜面的执行顺序(数值越小,优先级越高)
@Component  // 把 LoggerAspectJ 注册成bean,放到IOC容器中

public class LoggerAspectJ {
    /**
     * slf4j 日志接口
     */
    private static final Logger _log = LoggerFactory.getLogger(LoggerAspectJ.class);

    /**
     * 切面切入点表达式    引用的时候记得修改这里
     */
    private static final String ASPECT_POINTCUT_EXPRESSION = "execution(public * cn.sibat.subway.fare.controller..*.*(..))";

    /**
     * 定义一个方法用于声明切入点表达式,后面增强方法需要注解引用改方法名
     */
    @Pointcut(ASPECT_POINTCUT_EXPRESSION)
    public void aspectMethod() {

    }


    /**
     * 环绕增强，在某一方法的前后执行
     * <pre>
     *  记录web层控制器的操作日志信息,及相关异常信息.
     * </pre>
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("aspectMethod()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        // 日志开始时间
        long logBeginTime = System.currentTimeMillis();
        // 反射获取方法信息
        Class<?>[] params = ((MethodSignature) joinPoint.getSignature()).getMethod().getParameterTypes();
        // 代理方法信息
        String methodName = joinPoint.getSignature().getName();
        // 操作描述  这里反射获取注解
        LogAnnotation logAnnotation = joinPoint.getTarget().getClass().getDeclaredMethod(methodName, params)
                .getAnnotation(LogAnnotation.class);
        // 判断是否被注解类注解
        if (logAnnotation == null) {
            result = joinPoint.proceed(joinPoint.getArgs());
            return result;
        }
        _log.info("【Start】======================================================================【Start】");
        try {
            _log.info("日志开始时间： " + new Date());
            // 从上下文中获取request
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                    .getRequest();

            // 请求的url
            _log.info("请求url: " + request.getRequestURI() + "、" + request.getRequestURL());
            // 请求的ip
            _log.info("请求ip ：" + request.getRemoteAddr() + "、" + request.getHeader("x-forwarded-for") + "、"
                    + request.getHeader("Proxy-Client-IP") + "、" + request.getHeader("WL-Proxy-Client-IP"));
            // 请求的用户 TODO 这里的session属性需要自己根据项目修改
            _log.info("user: " + request.getSession().getAttribute("longStatus"));

            _log.info("操作描述：" + (logAnnotation.value().isEmpty() ? "默认无描述" : logAnnotation.value()));

            _log.info("方法名称：" + methodName);
            _log.info("方法对象：" + joinPoint.getTarget().getClass());
            _log.info("参数个数：" + params.length);

            // 传入参数
            StringBuffer buf = new StringBuffer("");
            // 判断是否有传入参数
            boolean bufFlag = false;
            // 控制循环
            int paramNext = 1;
            for (Object param : joinPoint.getArgs()) {
                String arg = null;
                if (param instanceof HttpServletRequest || param instanceof HttpServletResponse) {
                    continue;
                } else {
                    try {
                        // 这里使用的是fastJson解析
                        arg = (String) JSONObject.toJSON(param);
                    } catch (Exception e) {
                        _log.info("参数转换JSON数据失败！" + e.getMessage());
                        continue;
                    }
                    bufFlag = true;
                    buf.append("传参值" + paramNext + "为-" + arg + "\n");
                    paramNext++;
                }
            }
            _log.info(bufFlag ? buf.toString() : "没有传入参数");
            // 方法开始时间
            long beginTime = System.currentTimeMillis();
            _log.info("方法执行时间：" + new Date());
            // 执行方法
            result = joinPoint.proceed(joinPoint.getArgs());
            long endTime = System.currentTimeMillis();
            _log.info("方法结束时间：" + new Date());
            _log.info("方法执行耗时：" + (endTime - beginTime) + " 毫秒");
        } catch (Throwable e) {
            _log.error("日志操作异常\r\n" + e);
            throw e;
        } finally {
            long logEndTime = System.currentTimeMillis();
            _log.info("日志结束时间：" + new Date());
            _log.info("日志耗时：" + (logEndTime - logBeginTime) + " 毫秒");
            // TODO 这里可以做日志入库处理
            _log.info("【End】========================================================================【End】");
        }
        return result;
    }

}
