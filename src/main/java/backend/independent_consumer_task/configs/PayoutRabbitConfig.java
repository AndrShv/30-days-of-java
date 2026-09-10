package backend.independent_consumer_task.configs;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PayoutRabbitConfig {

    public static final String PAYOUT_EXCHANGE = "payouts.exchange";
    public static final String PAYOUT_QUEUE = "payouts.queue";
    public static final String DLQ_EXCHANGE = "payouts.dlx.exchange";
    public static final String DLQ_QUEUE = "payouts.dlq";
    public static final String PAYOUT_ROUTING_KEY = "payouts.routing.key";
    public static final String PAYOUT_ROUTING_KEY_DLQ = "payouts.routing.key.dlq";


    // -- Exchanges --

    @Bean
    public DirectExchange payoutExchange(){
        return new DirectExchange(PAYOUT_EXCHANGE);
    }


    @Bean
    public DirectExchange dlqExchange(){
        return new DirectExchange(DLQ_EXCHANGE);
    }

    // -- Queues --

    @Bean
    public Queue payoutQueue() {
        return QueueBuilder.durable(PAYOUT_QUEUE).build();
    }

    @Bean
    public Queue dlqQueue(){
        return QueueBuilder.durable(DLQ_QUEUE).build();
    }


    // -- Bindings --

    @Bean
    public Binding payoutBinding() {
        return BindingBuilder.bind(payoutQueue())
                .to(payoutExchange())
                .with(PAYOUT_ROUTING_KEY);
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(dlqQueue())
                .to(dlqExchange())
                .with(PAYOUT_ROUTING_KEY_DLQ);
    }




    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
