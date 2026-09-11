package backend.independent_consumer_example.aspects;

import backend.independent_consumer_example.annotations.Idempotent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class IdempotentAspect {

    private final StringRedisTemplate redisTemplate;
    private final ExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(idempotent)")
    public Object handleIdempotency(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        String keyExpression = idempotent.key();
        String messageId = parseSpelKey(keyExpression, joinPoint);

        String redisKey = "idempotency:event:" + messageId;

        Boolean isFirstTimeProcessed = redisTemplate.opsForValue().setIfAbsent(
                redisKey,
                "PROCESSED",
                idempotent.expireTime(),
                idempotent.timeUnit()
        );

        if (Boolean.FALSE.equals(isFirstTimeProcessed)) {
            log.warn("Duplicate message detected! Skipping processing for messageId: {}", messageId);
            return null;
        }

        try {
            log.info("First time processing for messageId: {}. Executing business logic...", messageId);
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            redisTemplate.delete(redisKey);
            log.error("Error processing messageId: {}. Redis key released for retry.", messageId, throwable);
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
