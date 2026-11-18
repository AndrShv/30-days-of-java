package backend.concurrency.tasks.executor;

import java.util.concurrent.Callable;

public class FetchBonusPointsTask implements Callable<Integer>{

    Integer bonus = 100;

    @Override
    public Integer call() throws Exception {
        Thread.sleep(1000);
        return bonus;
    }
}
