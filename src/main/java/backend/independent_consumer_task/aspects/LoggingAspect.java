package backend.independent_consumer_task.aspects;
import backend.independent_consumer_task.annotations.LogExecuteTimer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
@Order(2)
public class LoggingAspect {


    @Around("@annotation(logExecuteTimer)")
    public Object logTime(ProceedingJoinPoint joinPoint, LogExecuteTimer logExecuteTimer) throws Throwable {
        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            log.info("Method {} executed in {} ms", joinPoint.getSignature().toShortString(), executionTime);
            return result;
        } catch (Throwable throwable) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("Method {} failed after {} ms", joinPoint.getSignature().toShortString(), executionTime);
            throw throwable;
        }
    }

}
