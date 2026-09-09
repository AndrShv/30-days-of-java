package backend.independent_consumer_task.annotations;


import org.springframework.core.annotation.Order;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Order(1)
public @interface IdempotentPayout {
    String key();
    long ttlRedisTimeOut() default 5;
    TimeUnit timeUnit() default TimeUnit.MINUTES;
}
