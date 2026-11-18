package backend.liquid_ci_cd_example.configs;


import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMqConfig {

    @Value("${luquid.rabbitmq.exchange}")
    private  String exchangeName;

    @Value("${luquid.rabbitmq.dlx.exchange}")
    private String dlxExchangeName;

    @Value("${luquid.rabbitmq.queue}")
    private String queueName;

    @Value("${luquid.rabbitmq.dlx.queue}")
    private String dlxQueueName;


    @Value("${luquid.rabbitmq.routing-key}")
    private String routingKey;

    @Value("${luquid.rabbitmq.dlx.routing-key}")
    private String dlxRoutingKey;


    @Bean
    public DirectExchange liquidExchange() {
        return new DirectExchange(exchangeName);
    }
    @Bean
    public Queue liquidQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", exchangeName);
        args.put("x-dead-letter-routing-key", routingKey);
        return new Queue(queueName, true, false, false, args);
    }

    @Bean
    public Binding liquidBinding(){
        return BindingBuilder.bind(liquidQueue()).to(liquidExchange()).with(routingKey);
    }



    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(dlxExchangeName);
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue(dlxQueueName, true);
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with(dlxRoutingKey);
    }



}
