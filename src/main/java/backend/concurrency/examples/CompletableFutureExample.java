package backend.concurrency.examples;
import java.util.concurrent.CompletableFuture;

public class CompletableFutureExample {
    public static void main(String[] args) throws Exception {

        System.out.println("Запуск цепочки...");

        CompletableFuture.supplyAsync(() -> {
                    // Шаг 1: Асинхронно загружаем данные (в ForkJoinPool.commonPool())
                    simulateDelay(500);
                    System.out.println("1. Загрузили ID юзера в: " + Thread.currentThread().getName());
                    return 42;
                })
                .thenApply(userId -> {
                    // Шаг 2: Трансформируем результат (синхронно или в том же потоке)
                    System.out.println("2. Получаем имя по ID...");
                    return "User_" + userId;
                })
                .thenCompose(userName -> CompletableFuture.supplyAsync(() -> {
                    // Шаг 3: thenCompose для вложенных асинхронных вызовов (плоская цепочка)
                    simulateDelay(300);
                    return userName + " [Роль: ADMIN]";
                }))
                .exceptionally(throwable -> {
                    // Обработка ошибок в любой точке цепочки
                    System.err.println("Ошибка в цепочке: " + throwable.getMessage());
                    return "Default User";
                })
                .thenAccept(finalResult -> {
                    // Шаг 4: Потребляем итоговый результат
                    System.out.println("4. Финальный результат: " + finalResult);
                });

        // Ждем немного, чтобы демон-потоки успели завершиться перед выходом из main
        Thread.sleep(1500);
    }

    private static void simulateDelay(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
