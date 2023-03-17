package com.rumahorbo.login.aspect;

import com.rumahorbo.login.model.LogoutRequestDTO;
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
public class LogoutLoggerAspect {

    private final Logger logger = LoggerFactory.getLogger(LogoutLoggerAspect.class);
    private String username = "";

    @Pointcut(value = "@annotation(com.rumahorbo.login.annotation.LogoutLogger) && args(logoutRequestDTO,..)")
    public void logPointcut(LogoutRequestDTO logoutRequestDTO) {
    }

    @Before(value = "logPointcut(logoutRequestDTO)", argNames = "logoutRequestDTO")
    public void logBeforeAdvice(LogoutRequestDTO logoutRequestDTO) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String token = Objects.isNull(request.getHeader("Authorization")) ? "" : request.getHeader("Authorization").split(" ")[1];
        username = StringUtils.getNameByDecodeToken(token);
        String host = request.getHeader("Host");
        String agent = request.getHeader("User-Agent");

        logger.info("User '{}' trying to logout with agent '{}' and host '{}'", username, agent, host);
    }

    @AfterReturning(value = "logPointcut(logoutRequestDTO)", returning = "logoutResponseDTO", argNames = "logoutRequestDTO,logoutResponseDTO")
    public void logAfterAdvice(LogoutRequestDTO logoutRequestDTO, ResponseEntity<?> logoutResponseDTO) {
        if (logoutResponseDTO.getStatusCode() != HttpStatus.OK) {
            logger.info("User '{}' failed to logout", username);
        } else {
            logger.info("User '{}' succeed to logout", username);
        }
    }

    @AfterThrowing(value = "logPointcut(logoutRequestDTO)", argNames = "logoutRequestDTO")
    public void logThrowAdvice(LogoutRequestDTO logoutRequestDTO) {
        logger.info("User '{}' failed to logout", username);
    }

    @Around(value = "logPointcut(logoutRequestDTO)", argNames = "proceedingJoinPoint,logoutRequestDTO")
    public Object logAround(ProceedingJoinPoint proceedingJoinPoint, LogoutRequestDTO logoutRequestDTO) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object value = proceedingJoinPoint.proceed();
        long stopTime = System.currentTimeMillis();
        long time = stopTime - startTime;
        logger.info("Time to executed '{}' is '{}' milliseconds", proceedingJoinPoint.getSignature().getName(), time);
        return value;
    }

}
