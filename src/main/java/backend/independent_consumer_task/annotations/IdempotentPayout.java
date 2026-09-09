package backend.independent_consumer_task.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IdempotentPayout {
    String key();
    long ttlRedisTimeOut() default 5;
    TimeUnit timeUnit() default TimeUnit.MINUTES;
}
