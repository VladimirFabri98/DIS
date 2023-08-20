package vladimir.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfiguration {

	
	@Bean
	public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
		
		return builder
				.routes()
				.route(p -> p.path("/game-composite/**").uri("lb://game-composite"))
//				.route(p -> p.path("/game/**").uri("lb://game"))
//				.route(p -> p.path("/review/**").uri("lb://review"))
//				.route(p -> p.path("/dlc/**").uri("lb://dlc"))
//				.route(p -> p.path("/event/**").uri("lb://game-event"))
				.route(p -> p.path("/eureka/api/{segment}").filters(f -> f.setPath("/eureka/{segment}"))
						.uri("http://eureka:8761"))
				.route(p -> p.path("/eureka/web").filters(f -> f.setPath("/"))
						.uri("http://eureka:8761"))
				.route(p -> p.path("/eureka/**").uri("http://eureka:8761"))
				.route(p -> p.host("i.feel.lucky:8080").and().path("/headerrouting/**")
						.filters(f -> f.setPath("/200"))
						.uri("http://httpstat.us"))
				.build();
	}
}
