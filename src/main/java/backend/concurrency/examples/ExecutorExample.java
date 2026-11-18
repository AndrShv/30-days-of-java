package backend.concurrency.examples;
import java.util.concurrent.*;

public class ExecutorExample {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // Создаем пул из 2 потоков
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // 1. Runnable — просто выполняем задачу без возврата значения
        Runnable runnableTask = () -> {
            System.out.println("Runnable выполняется в потоке: " + Thread.currentThread().getName());
        };
        executor.execute(runnableTask);

        // 2. Callable — выполняем задачу и возвращаем значение
        Callable<String> callableTask = () -> {
            Thread.sleep(1000); // Симуляция долгой работы
            return "Результат из " + Thread.currentThread().getName();
        };

        // submit() возвращает Future
        Future<String> future = executor.submit(callableTask);

        System.out.println("Основной поток продолжается...");

        // get() — БЛОКИРУЮЩИЙ вызов. Ждем, пока задача завершится
        String result = future.get();
        System.out.println("Получен результат: " + result);

        // Обязательно закрываем пул!
        executor.shutdown();
    }
}