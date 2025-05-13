package app;

import app.models.CollectionBox;
import app.services.CollectionBoxService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.UUID;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "app.repositories")
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner loadData(CollectionBoxService service) {
        return args -> {
            System.out.println("Creating sample collection boxes...");

            // Register new boxes
            CollectionBox box1 = service.registerBox();
            CollectionBox box2 = service.registerBox();
            CollectionBox box3 = service.registerBox();

            System.out.println("Created boxes with IDs: " + box1.getUuid() + ", " + box2.getUuid() + ", " + box3.getUuid());

            // Put money in the first box
            service.putMoney(box1.getUuid(), "PLN", 100.0);
            service.putMoney(box1.getUuid(), "EUR", 50.0);

            // Empty the second box (already empty, just to demonstrate)
            service.emptyBox(box2.getUuid());

            System.out.println("Sample data created successfully.");
        };
    }
}
