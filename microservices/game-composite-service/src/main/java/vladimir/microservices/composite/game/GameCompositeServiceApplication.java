package vladimir.microservices.composite.game;

import static java.util.Collections.emptyList;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;
import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.reactive.function.client.WebClient;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spring.web.plugins.Docket;
import vladimir.microservices.composite.game.services.GameCompositeIntegration;

@SpringBootApplication
@ComponentScan("vladimir")
public class GameCompositeServiceApplication {

	@Value("${api.common.version}")
	String apiVersion;
	@Value("${api.common.title}")
	String apiTitle;
	@Value("${api.common.description}")
	String apiDescription;
	@Value("${api.common.termsOfServiceUrl}")
	String apiTermsOfServiceUrl;
	@Value("${api.common.license}")
	String apiLicense;
	@Value("${api.common.licenseUrl}")
	String apiLicenseUrl;
	@Value("${api.common.contact.name}")
	String apiContactName;
	@Value("${api.common.contact.url}")
	String apiContactUrl;
	@Value("${api.common.contact.email}")
	String apiContactEmail;

	@SuppressWarnings("deprecation")
	@Bean
	public Docket apiDocumentation() {

		return new Docket(SWAGGER_2).select().apis(basePackage("vladimir.microservices.composite.game"))
				.paths(PathSelectors.any()).build().globalResponseMessage(GET, emptyList())
				.apiInfo(new ApiInfo(apiTitle, apiDescription, apiVersion, apiTermsOfServiceUrl,
						new Contact(apiContactName, apiContactUrl, apiContactEmail), apiLicense, apiLicenseUrl,
						emptyList()));
	}
	
	@Autowired
	GameCompositeIntegration integration;
	
	@Bean
	@LoadBalanced
	public WebClient.Builder loadBalancedWebClientBuilder(){
		final WebClient.Builder builder = WebClient.builder();
		return builder;
	}

	
	public static void main(String[] args) {
		SpringApplication.run(GameCompositeServiceApplication.class, args);
	}

}
