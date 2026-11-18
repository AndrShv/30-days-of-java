package backend.competing_consumers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CompetingConsumersApp {
    public static void main(String[] args) {
        SpringApplication.run(CompetingConsumersApp.class, args);
        //mvn clean package -Dstart-class=backend.competing_consumers.CompetingConsumersApp
        //mvn clean install -Dstart-class=backend.competing_consumers.CompetingConsumersApp
    }
}
