package vladimir.microservices.core.dlc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("vladimir")
public class DlcServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DlcServiceApplication.class, args);
	}

}
