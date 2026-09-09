package backend.independent_consumer_task.aspects;


import backend.independent_consumer_task.annotations.IdempotentPayout;
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
public class PaymentAspect {
    private final StringRedisTemplate redisTemplate;
    private final ExpressionParser parser = new SpelExpressionParser();


    @Around("@annotation(idempotentPayout)")
    public Object handleIdempotency(ProceedingJoinPoint joinPoint, IdempotentPayout idempotentPayout) throws Throwable {
        String payoutKey = idempotentPayout.key();
        String payoutId = parsePayoutKey(payoutKey, joinPoint);
        String redisKey = "payout:" + payoutId;


        Boolean isFirstPayment = redisTemplate.opsForValue()
                .setIfAbsent(redisKey,
                        "IN_PROGRESS",
                        idempotentPayout.ttlRedisTimeOut(),
                        idempotentPayout.timeUnit()
                );

        if (Boolean.FALSE.equals(isFirstPayment)) {
            log.warn("Payout with id {} is already processed", payoutId);
            return null;
        }

        try {
            log.info("Processing payout with id {}", payoutId);

            Object result  = joinPoint.proceed();

            redisTemplate.opsForValue().set(
                    redisKey,
                    "COMPLETED",
                    idempotentPayout.ttlRedisTimeOut(),
                    idempotentPayout.timeUnit()
            );

            return result;



        } catch (Throwable throwable) {
            redisTemplate.delete(redisKey);
            log.error("Error while processing payout with id {}", payoutId, throwable);
            throw throwable;
        }

    }



    private String parsePayoutKey(String payoutKey, ProceedingJoinPoint joinPoint) {
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

        return parser.parseExpression(payoutKey).getValue(context, String.class);
    }
}
