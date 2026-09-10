package backend.transactional_outbox_pattern.configs;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMqConfig {

    @Value("${outbox.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${outbox.rabbitmq.queue}")
    private String queueName;

    @Value("${outbox.rabbitmq.routing-key}")
    private String routingKey;

    @Value("${outbox.rabbitmq.dlx}")
    private String dlxName;

    @Value("${outbox.rabbitmq.dlq}")
    private String dlqName;

    @Value("${outbox.rabbitmq.dlq-routing-key}")
    private String dlqRoutingKey;

    // --- Main Exchange & Queue ---

    @Bean
    public DirectExchange mainExchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Queue mainQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", dlxName);
        args.put("x-dead-letter-routing-key", dlqRoutingKey);
        return new Queue(queueName, true, false, false, args);
    }

    @Bean
    public Binding mainBinding() {
        return BindingBuilder.bind(mainQueue()).to(mainExchange()).with(routingKey);
    }

    // --- Dead Letter Exchange & Queue (DLQ) ---

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(dlxName);
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue(dlqName, true);
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with(dlqRoutingKey);
    }
}