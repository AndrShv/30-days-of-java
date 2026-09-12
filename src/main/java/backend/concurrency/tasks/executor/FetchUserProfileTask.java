package backend.concurrency.tasks.executor;

import java.util.concurrent.Callable;


public class FetchUserProfileTask implements Callable<UserProfile>{
    @Override
    public UserProfile call() throws Exception {
        Thread.sleep(1500);
        return new UserProfile(
                "John Doe",
                "john.doe@example.com"
        );
    }
}
