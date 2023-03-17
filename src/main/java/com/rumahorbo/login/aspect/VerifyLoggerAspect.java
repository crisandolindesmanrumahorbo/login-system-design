package com.rumahorbo.login.aspect;

import com.rumahorbo.login.util.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Component
@Aspect
public class VerifyLoggerAspect {

    private final Logger logger = LoggerFactory.getLogger(VerifyLoggerAspect.class);
    private String username;
    private String url;

    @Pointcut(value = "@annotation(com.rumahorbo.login.annotation.VerifyLogger)")
    public void logPointcut() {
    }

    @Before(value = "logPointcut()")
    public void logBeforeAdvice() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String token = Objects.isNull(request.getHeader("Authorization")) ? "" : request.getHeader("Authorization").split(" ")[1];
        username = StringUtils.getNameByDecodeToken(token);
        url = request.getHeader("X-Original-URI");
        String agent = request.getHeader("User-Agent");
        String host = Objects.isNull(request.getHeader("Host")) ? "" : request.getHeader("Host");

        logger.info("User '{}' trying to access '{}' with agent '{}' and host '{}'", username, url, agent, host);
    }

    @AfterReturning(value = "logPointcut()", returning = "response")
    public void logAfterAdvice(ResponseEntity<?> response) {
        if (response.getStatusCode() != HttpStatus.OK) {
            logger.info("User '{}' failed to access '{}'", username, url);
        } else {
            logger.info("User '{}' succeed to access '{}'", username, url);
        }
    }

    @AfterThrowing(value = "logPointcut()")
    public void logThrowAdvice() {
        logger.info("User '{}' failed to access '{}'", username, url);
    }

    @Around(value = "logPointcut()", argNames = "proceedingJoinPoint")
    public Object logAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object value = proceedingJoinPoint.proceed();
        long stopTime = System.currentTimeMillis();
        long time = stopTime - startTime;
        logger.info("Time to executed '{}' is '{}' milliseconds", proceedingJoinPoint.getSignature().getName(), time);
        return value;
    }

}
