package backend.concurrency.tasks.executor;

import java.util.concurrent.Callable;

public class FetchOrderSummaryTask implements Callable<OrderSummary>{

    @Override
    public OrderSummary call() throws Exception {
        Thread.sleep(2000);
        return new OrderSummary(15000.50, 5);
    }
}
