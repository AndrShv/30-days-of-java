package backend.transactional_outbox_pattern_task.configs;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMqConfig {

    @Value("${order.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${order.rabbitmq.queue}")
    private String queueName;

    @Value("${order.rabbitmq.routing-key}")
    private String routingKey;

    @Value("${order.rabbitmq.dlx}")
    private String dlxName;

    @Value("${order.rabbitmq.dlq}")
    private String dlqName;

    @Value("${order.rabbitmq.dlq-routing-key}")
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