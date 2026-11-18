package backend.concurrency.tasks.completable_future;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class FraudAndStockCheck {

    public static void main(String[] args) throws InterruptedException {
        CompletableFuture<Void> bookingPipeline = CompletableFuture
                .supplyAsync(() -> {
                    simulateDelay(300);
                    Random random = new Random();
                    int orderId = random.nextInt(1000);
                    System.out.println("1. Заказ #" + orderId + " сгенерирован");
                    return orderId;
                })
                .thenCompose(orderId -> {
                    CompletableFuture<Boolean> stockTask = CompletableFuture.supplyAsync(
                            () -> checkTheAvailability(orderId)
                    );
                    CompletableFuture<Boolean> moneyTask = CompletableFuture.supplyAsync(
                            () -> checkTheAmountOfMoney(5000.0)
                    );

                    return stockTask.thenCombine(moneyTask, (isAvailable, isMoneyEnough) -> {
                        if (!isAvailable || !isMoneyEnough) {
                            throw new RuntimeException("Проверка склада или баланса не пройдена");
                        }
                        return "Чек сгенерирован для заказа #" + orderId;
                    });
                })
                .exceptionally(ex -> {
                    System.err.println("❌ Ошибка в процессе заказа: " + ex.getMessage());
                    return "Заказ ОТМЕНЕН: произошла ошибка";
                })
                .thenAccept(finalResult -> {
                    System.out.println("Результат: " + finalResult);
                });

        System.out.println(">>> Основной поток (main) свободен и занимается другими делами...\n");
        Thread.sleep(2000);
    }

    private static void simulateDelay(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static boolean checkTheAvailability(int orderId) {
        simulateDelay(300);
        Random random = new Random();
        boolean isAvailable = random.nextBoolean();
        System.out.println("2a. Проверка склада для #" + orderId + ": " + isAvailable);
        if (!isAvailable) {
            throw new RuntimeException("Товара нет на складе");
        }
        return true;
    }

    private static boolean checkTheAmountOfMoney(double amountOfMoney) {
        simulateDelay(300);
        Random random = new Random();
        int randomPrice = 1000 + random.nextInt(9000);
        System.out.println("2b. Проверка денег (Доступно: " + amountOfMoney + ", Требуется: " + randomPrice + ")");
        if (amountOfMoney < randomPrice) {
            throw new RuntimeException("Недостаточно средств");
        }
        return true;
    }
}