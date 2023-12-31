package vladimir.gateway;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment=RANDOM_PORT, properties = {"eureka.client.enabled=false","spring.cloud.config.enabled=false"})
public class GatewayApplicationTests {

	@Test
	public void contextLoads() {
	}

}