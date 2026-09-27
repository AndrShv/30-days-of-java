///
package backend.concurrency.examples;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class FlightBookingSystem {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Начало процесса бронирования ===");

        // Запускаем асинхронный пайплайн
        CompletableFuture<Void> bookingPipeline = CompletableFuture
                // Шаг 1: Генерируем ID бронирования
                .supplyAsync(() -> {
                    simulateDelay(300);
                    Random random = new Random();
                    return 100000 + random.nextInt(900000);
                })
                .thenCompose(bookingId -> fetchFlightDetailsAsync(bookingId))
                // Шаг 3: Параллельно списываем деньги и отправляем SMS, затем объединяем их результаты (thenCombine)
                .thenCompose(flightDetails -> {
                    CompletableFuture<String> paymentTask = processPaymentAsync(500);
                    CompletableFuture<String> smsTask = sendSmsNotificationAsync();

                    return paymentTask.thenCombine(smsTask, (paymentResult, smsResult) -> {
                        return flightDetails + " | " + paymentResult + " | " + smsResult;
                    });
                })
                // Шаг 4: Обработка ошибок в любой точке пайплайна
                .exceptionally(ex -> {
                    System.err.println("❌ Ошибка в процессе бронирования: " + ex.getMessage());
                    return "Бронирование ОТМЕНЕНО: произошла ошибка";
                })
                // Шаг 5: Итоговый потребитель результата
                .thenAccept(finalResult -> {
                    System.out.println("\n🎉 ФИНАЛЬНЫЙ СТАТУС:");
                    System.out.println(finalResult);
                });

        System.out.println(">>> Основной поток (main) свободны и занимается другими делами...\n");

        // Ждем немного, чтобы фоновые потоки из ForkJoinPool успели дописать в консоль
        Thread.sleep(2000);
    }

    // --- Вспомогательные асинхронные сервисы ---

    private static CompletableFuture<String> fetchFlightDetailsAsync(int bookingId) {
        return CompletableFuture.supplyAsync(() -> {
            simulateDelay(500);
            System.out.println("2. Получена информация о рейсе [Поток: " + Thread.currentThread().getName() + "]");
            return "Рейс №" + bookingId + ": NYC-London, Seat 12B";
        });
    }

    private static CompletableFuture<String> processPaymentAsync(double amount) {
        return CompletableFuture.supplyAsync(() -> {
            simulateDelay(400);
            System.out.println("3a. Оплата $" + amount + " успешно списана [Поток: " + Thread.currentThread().getName() + "]");
            return "Paid $" + amount;
        });
    }

    private static CompletableFuture<String> sendSmsNotificationAsync() {
        return CompletableFuture.supplyAsync(() -> {
            simulateDelay(200);
            System.out.println("3b. SMS-уведомление отправлено [Поток: " + Thread.currentThread().getName() + "]");
            return "SMS sent";
        });
    }

    private static void simulateDelay(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}