package com.rumahorbo.login.aspect;

import com.rumahorbo.login.model.LoginRequestDTO;
import com.rumahorbo.login.model.LoginResponseDTO;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class AuthenticationAspect {

    private final Logger logger = LoggerFactory.getLogger(AuthenticationAspect.class);

    @Pointcut(value = "@annotation(com.rumahorbo.login.annotation.Log) && args(loginRequestDTO,..)")
    public void logPointcut(LoginRequestDTO loginRequestDTO) {
    }

    @Before(value = "logPointcut(loginRequestDTO)", argNames = "loginRequestDTO")
    public void logBeforeAdvice(LoginRequestDTO loginRequestDTO) {
        logger.info("User " + loginRequestDTO.username() + " trying to login");
    }

    @AfterReturning(value = "logPointcut(loginRequestDTO)", returning = "loginResponseDTO", argNames = "loginRequestDTO,loginResponseDTO")
    public void logAfterAdvice(LoginRequestDTO loginRequestDTO, LoginResponseDTO loginResponseDTO) {
        if (loginResponseDTO == null) {
            logger.info("User " + loginRequestDTO.username() + " failed to login");
        } else {
            logger.info("User " + loginRequestDTO.username() + " succeed to login");
        }
    }

    @AfterThrowing(value = "logPointcut(loginRequestDTO)", argNames = "loginRequestDTO")
    public void logThrowAdvice(LoginRequestDTO loginRequestDTO) {
        logger.info("User " + loginRequestDTO.username() + " failed to login");
    }

    @Around(value = "logPointcut(loginRequestDTO)", argNames = "proceedingJoinPoint,loginRequestDTO")
    public Object logAround(ProceedingJoinPoint proceedingJoinPoint, LoginRequestDTO loginRequestDTO) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object value = proceedingJoinPoint.proceed();
        long stopTime = System.currentTimeMillis();
        long time = stopTime - startTime;
        logger.info("Time to executed " + proceedingJoinPoint.getSignature().getName() + " is " + time + " milliseconds");
        return value;
    }

}
