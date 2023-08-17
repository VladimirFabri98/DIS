package vladimir.gateway;

import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.CompositeReactiveHealthIndicator;
import org.springframework.boot.actuate.health.DefaultReactiveHealthIndicatorRegistry;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.boot.actuate.health.ReactiveHealthIndicatorRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Configuration
public class HealthCheckConfiguration {

	private static final Logger LOG = LoggerFactory.getLogger(HealthCheckConfiguration.class);

	private HealthAggregator healthAggregator;

	private final WebClient.Builder webClientBuilder;

	private WebClient webClient;

	@Autowired
	public HealthCheckConfiguration(WebClient.Builder webClientBuilder, HealthAggregator healthAggregator) {
		this.webClientBuilder = webClientBuilder;
		this.healthAggregator = healthAggregator;
	}

	@Bean
	ReactiveHealthIndicator healthcheckMicroservices() {

		ReactiveHealthIndicatorRegistry registry = new DefaultReactiveHealthIndicatorRegistry(new LinkedHashMap<>());

		registry.register("game", () -> getHealth("http://game"));
		registry.register("dlc", () -> getHealth("http://dlc"));
		registry.register("review", () -> getHealth("http://review"));
		registry.register("game-event", () -> getHealth("http://game-event"));
		registry.register("game-composite", () -> getHealth("http://game-composite"));

		return new CompositeReactiveHealthIndicator(healthAggregator, registry);
	}

	private Mono<Health> getHealth(String url) {
		url += "/actuator/health";
		LOG.debug("Will call the Health API on URL: {}", url);
		return getWebClient().get().uri(url).retrieve().bodyToMono(String.class)
				.map(s -> new Health.Builder().up().build())
				.onErrorResume(ex -> Mono.just(new Health.Builder().down(ex).build())).log();
	}

	private WebClient getWebClient() {
		if (webClient == null) {
			webClient = webClientBuilder.build();
		}
		return webClient;
	}

}
