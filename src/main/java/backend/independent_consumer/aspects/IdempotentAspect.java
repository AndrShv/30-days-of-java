package backend.independent_consumer.aspects;

import backend.independent_consumer.annotations.Idempotent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Duration;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
@Order(1)
public class IdempotentAspect {

    private final StringRedisTemplate redisTemplate;
    private final ExpressionParser parser = new SpelExpressionParser();

    private static final String STATE_IN_PROGRESS = "IN_PROGRESS";
    private static final String STATE_COMPLETED = "COMPLETED";

    @Around("@annotation(idempotent)")
    public Object handleIdempotency(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        String transactionId = parseSpelKey(idempotent.key(), joinPoint);
        String redisKey = "idempotency:bank-tx:" + transactionId;

        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(
                redisKey,
                STATE_IN_PROGRESS,
                Duration.ofSeconds(idempotent.lockTimeoutSeconds())
        );

        if (Boolean.FALSE.equals(acquired)) {
            String currentState = redisTemplate.opsForValue().get(redisKey);
            log.warn("Duplicate bank transaction detected! transactionId: {}, currentState: {}. Skipping.",
                    transactionId, currentState);
            return null;
        }

        try {
            log.info("Processing bank transaction: transactionId={}...", transactionId);

            Object result = joinPoint.proceed();

            // 2. Успех: переводим статус в COMPLETED и задаем долгосрочный TTL
            redisTemplate.opsForValue().set(
                    redisKey,
                    STATE_COMPLETED,
                    idempotent.expireTime(),
                    idempotent.timeUnit()
            );
            log.info("Transaction {} marked as COMPLETED in Redis.", transactionId);

            return result;

        } catch (Throwable throwable) {
            redisTemplate.delete(redisKey);
            log.error("Failed to process transactionId: {}. Lock released for Retry/DLQ.", transactionId, throwable);
            throw throwable;
        }
    }

    private String parseSpelKey(String keyExpression, ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        String[] parameterNames = signature.getParameterNames();

        EvaluationContext context = new StandardEvaluationContext();
        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }
        }

        return parser.parseExpression(keyExpression).getValue(context, String.class);
    }
}