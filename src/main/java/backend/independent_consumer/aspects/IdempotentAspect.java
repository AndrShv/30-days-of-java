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

        /*
         * ШАГ 1: Попытка "забронировать" обработку транзакции
         * -------------------------------------------------------------------------------------
         * setIfAbsent — это атомарная команда Redis "SETNX" (Set if Not Exists).
         * Поведение:
         *   - Если ключа в Redis НЕТ -> создает его со значением "IN_PROGRESS" и возвращает true.
         *   - Если ключ УЖЕ ЕСТЬ -> ничего не делает и возвращает false.
         *
         * lockTimeoutSeconds (60 сек) — страховка от падения приложения.
         * Если сервис сдохнет прямо во время списывания денег, Redis сам удалит ключ через 60 сек,
         * и повторная попытка (Retry) не заблокируется навечно.
         */
        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(
                redisKey,
                STATE_IN_PROGRESS,
                Duration.ofSeconds(idempotent.lockTimeoutSeconds())
        );




        /*
         * ШАГ 2: Реакция на дубликат
         * -------------------------------------------------------------------------------------
         * Если acquired == false (или null), значит, транзакцию с таким UUID уже кто-то делает
         * прямо сейчас (IN_PROGRESS) или сделал ранее (COMPLETED).
         */
        if (Boolean.FALSE.equals(acquired)) {
            String currentState = redisTemplate.opsForValue().get(redisKey);
            log.warn("Duplicate bank transaction detected! transactionId: {}, currentState: {}. Skipping.", transactionId, currentState);
            return null;
        }

        try {
            log.info("Processing bank transaction: transactionId={}...", transactionId);

            Object result = joinPoint.proceed();

            /*
             * ШАГ 3: Успешное финализирование
             * -------------------------------------------------------------------------------------
             * Если бизнес-логика выполнилась БЕЗ ошибок:
             * Меняем статус в Redis с "IN_PROGRESS" на "COMPLETED" и ставим ДОЛГИЙ TTL (например, 24 часа).
             * Теперь в течение суток любые повторные прилеты этого transactionId будут отсекаться.
             */
            redisTemplate.opsForValue().set(
                    redisKey,
                    STATE_COMPLETED,
                    idempotent.expireTime(),
                    idempotent.timeUnit()
            );
            log.info("Transaction {} marked as COMPLETED in Redis.", transactionId);

            return result;

        } catch (Throwable throwable) {

            /*
             * ШАГ 4: Обработка ошибок в бизнес-логике
             * -------------------------------------------------------------------------------------
             * Если бизнес-логика упала (вылетело исключение):
             * Удаляем ключ из Redis!
             * Зачем? Чтобы освободить транзакцию. Когда сработает механизм Retry или сообщение
             * переотправится из DLQ, аспект снова пропустит этот transactionId на повторную попытку.
             */
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