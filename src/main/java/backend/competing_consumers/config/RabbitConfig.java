package backend.competing_consumers.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class RabbitConfig {

    public static final String TASK_QUEUE = "task.process.queue";

    @Bean
    public Queue taskQueue() {
        return new Queue(TASK_QUEUE, true);
    }


    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter()); // <- Подключаем JSON
        // 1. Устанавливаем количество параллельных потоков-консьюмеров внутри ОДНОГО приложения
        factory.setConcurrentConsumers(3); // 3 воркера под капотом
        factory.setMaxConcurrentConsumers(5); // 5 воркеров максимум, если нагрузка возрастет
        // 2. КЛЮЧЕВОЙ ПАРАМЕТР SYSTEM DESIGN: FAIR DISPATCH
        // Каждый воркер берет строго по 1 задаче за раз.
        // Следующую получит только после отправки ACK за предыдущую.
        factory.setPrefetchCount(1);

        return factory;
    }
}
