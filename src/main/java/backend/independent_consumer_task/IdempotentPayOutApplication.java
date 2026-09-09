package backend.independent_consumer_task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class IdempotentPayOutApplication {

    public static void main(String[] args) {
        SpringApplication.run(IdempotentPayOutApplication.class, args);
    }
}
