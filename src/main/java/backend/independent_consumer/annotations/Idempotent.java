package backend.independent_consumer.annotations;

import org.springframework.core.annotation.Order;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Order(1)
public @interface Idempotent {
    String key();
    long expireTime() default 24;
    TimeUnit timeUnit() default TimeUnit.HOURS;
    long lockTimeoutSeconds() default 60;
}
