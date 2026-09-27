package backend.competing_consumers.rest;


import backend.competing_consumers.config.RabbitConfig;
import backend.competing_consumers.dto.TaskMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskRestController {

    private final RabbitTemplate rabbitTemplate;
    private final Random random = new Random();


    @PostMapping("/generate")
    public ResponseEntity<String> generateTasks() {
        // Засылаем 5 задач с разным временем выполнения
        for (int i = 1; i <= 5; i++) {
            String taskId = "TASK-" + i + "-" + UUID.randomUUID().toString().substring(0, 4);
            int duration = random.nextInt(4) + 2; // от 2 до 5 секунд

            TaskMessage message = TaskMessage.builder()
                    .taskId(taskId)
                    .durationInSeconds(duration)
                    .build();
            rabbitTemplate.convertAndSend(RabbitConfig.TASK_QUEUE, message);
        }

        return ResponseEntity.ok("5 задач успешно отправлены в очередь!");
    }
}