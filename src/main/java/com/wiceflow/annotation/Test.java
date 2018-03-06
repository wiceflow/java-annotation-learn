package com.wiceflow.annotation;

import com.wiceflow.annotation.util.LoggerAspectJ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author BF
 * @date 2018/3/6
 */
public class Test {
    /**
     * slf4j 日志接口
     */
    private static final Logger _log = LoggerFactory.getLogger(LoggerAspectJ.class);
    public static void main(String[] args) {
        long beginTime = new Date().getTime();
        System.out.println(new Date());
        _log.debug(""+new Date());
    }
}
