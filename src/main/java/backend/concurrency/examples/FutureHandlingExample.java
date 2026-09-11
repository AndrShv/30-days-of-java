package backend.concurrency.examples;

import java.util.concurrent.*;

public class FutureHandlingExample {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Future<String> future = executor.submit(() -> {
            Thread.sleep(3000);
            return "Успешно завершено";
        });

        try {
            // Ждем результат МАКСИМУМ 1 секунду, чтобы не зависнуть навечно
            String result = future.get(1, TimeUnit.SECONDS);
            System.out.println(result);
        } catch (TimeoutException e) {
            System.err.println("Время ожидания истекло! Отменяем задачу...");
            // Прерываем поток, если он еще выполняется (true)
            future.cancel(true);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Задача отменена? " + future.isCancelled());
            executor.shutdown();
        }
    }
}
