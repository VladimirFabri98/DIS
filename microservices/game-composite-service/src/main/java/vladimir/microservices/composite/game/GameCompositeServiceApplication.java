package vladimir.microservices.composite.game;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("vladimir")
public class GameCompositeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GameCompositeServiceApplication.class, args);
	}

}
