package backend.competing_consumers.services;

import backend.competing_consumers.config.RabbitConfig;
import backend.competing_consumers.dto.TaskMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TaskWorker {

    private static final Logger log = LoggerFactory.getLogger(TaskWorker.class);

    @RabbitListener(queues = RabbitConfig.TASK_QUEUE)
    public void processTask(TaskMessage task) {
        String threadName = Thread.currentThread().getName();
        log.info("[Worker: {}] 🚀 Взял в работу задачу: {} (Займет {} сек)", threadName, task.getTaskId(), task.getDurationInSeconds());

        try {
            Thread.sleep(task.getDurationInSeconds() * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Обработка прервана", e);
        }

        log.info("[Worker: {}] ✅ Завершил задачу: {}", threadName, task.getTaskId());
    }
}