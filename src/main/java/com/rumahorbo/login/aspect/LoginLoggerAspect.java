package com.rumahorbo.login.aspect;

import com.rumahorbo.login.model.LoginRequestDTO;
import com.rumahorbo.login.model.LoginResponseDTO;
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
public class LoginLoggerAspect {

    private final Logger logger = LoggerFactory.getLogger(LoginLoggerAspect.class);

    @Pointcut(value = "@annotation(com.rumahorbo.login.annotation.LoginLogger) && args(loginRequestDTO,..)")
    public void logPointcut(LoginRequestDTO loginRequestDTO) {
    }

    @Before(value = "logPointcut(loginRequestDTO)", argNames = "loginRequestDTO")
    public void logBeforeAdvice(LoginRequestDTO loginRequestDTO) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String host = request.getHeader("Host");
        String agent = request.getHeader("User-Agent");

        logger.info("User '{}' trying to login with agent '{}' and host '{}'", loginRequestDTO.username(), agent, host);
    }

    @AfterReturning(value = "logPointcut(loginRequestDTO)", returning = "loginResponse", argNames = "loginRequestDTO,loginResponse")
    public void logAfterAdvice(LoginRequestDTO loginRequestDTO, ResponseEntity<LoginResponseDTO> loginResponse) {
        if (loginResponse.getStatusCode() != HttpStatus.OK) {
            logger.info("User '{}' failed to login", loginRequestDTO.username());
        } else {
            logger.info("User '{}' succeed to login", loginRequestDTO.username());
        }
    }

    @AfterThrowing(value = "logPointcut(loginRequestDTO)", argNames = "loginRequestDTO")
    public void logThrowAdvice(LoginRequestDTO loginRequestDTO) {
        logger.info("User '{}' failed to login", loginRequestDTO.username());
    }

    @Around(value = "logPointcut(loginRequestDTO)", argNames = "proceedingJoinPoint,loginRequestDTO")
    public Object logAround(ProceedingJoinPoint proceedingJoinPoint, LoginRequestDTO loginRequestDTO) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object value = proceedingJoinPoint.proceed();
        long stopTime = System.currentTimeMillis();
        long time = stopTime - startTime;
        logger.info("Time to executed '{}' is '{}' milliseconds", proceedingJoinPoint.getSignature().getName(), time);
        return value;
    }

}
