package backend.concurrency.tasks.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ReportAggregator {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        long startTime = System.currentTimeMillis();
        Future<UserProfile> profileFuture = executorService.submit(new FetchUserProfileTask());
        Future<OrderSummary> ordersFuture = executorService.submit(new FetchOrderSummaryTask());
        Future<Integer> bonusFuture = executorService.submit(new FetchBonusPointsTask());

        try {
            UserProfile profile = profileFuture.get();
            OrderSummary orders = ordersFuture.get();
            Integer bonusPoints = bonusFuture.get();

            long totalTime = System.currentTimeMillis() - startTime;

            UserReport report = new UserReport(profile, orders, bonusPoints, totalTime);
            System.out.println("Отчёт успешно сформирован за " + totalTime + " ms:");
            System.out.println(report);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }
}