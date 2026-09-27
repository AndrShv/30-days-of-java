package backend.independent_consumer.aspects;


import backend.independent_consumer.annotations.LogTimer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Order(2)
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LogAspect {
    private final StringRedisTemplate redisTemplate;
    private final ExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(logTimer)")
    public Object handleTimer(ProceedingJoinPoint joinPoint, LogTimer logTimer) {

        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now();
        String duration = endTime.toString().substring(11, 19);
        log.info("Start time: {}", startTime);
        log.info("End time: {}", endTime);
        log.info("Duration: {}", duration);

        return null;
    }




}
