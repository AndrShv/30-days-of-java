package backend.concurrency.examples;

import java.util.concurrent.Executors;

public class VirtualThreadsExample {
    public static void main(String[] args) {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {

            for (int i = 0; i < 10_000; i++) {
                final int taskId = i;
                executor.submit(() -> {
                    // Виртуальные потоки блокируются БЕЗ блокировки системного потока ОС (Carrier thread)
                    Thread.sleep(1000);
                    return taskId;
                });
            }
            // try-with-resources автоматически вызывает shutdown() и ждет завершения всех 10,000 виртуальных потоков!
        }
        System.out.println("Завершено 10,000 параллельных задач!");
    }
}
