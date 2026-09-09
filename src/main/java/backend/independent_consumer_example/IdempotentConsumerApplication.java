package backend.independent_consumer_example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class IdempotentConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(IdempotentConsumerApplication.class, args);
    }
}
