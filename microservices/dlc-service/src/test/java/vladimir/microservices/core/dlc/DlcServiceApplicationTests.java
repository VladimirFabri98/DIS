package vladimir.microservices.core.dlc;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
class DlcServiceApplicationTests {

	@Autowired
	private WebTestClient client;
	
	@Test
	public void getDlcsByProductId() {

		int gameId = 1;

		client.get()
			.uri("/dlc/" + gameId)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.length()").isEqualTo(3)
			.jsonPath("$[0].gameId").isEqualTo(gameId);
	}

	@Test
	public void getDlcsMissingParameter() {
	
		client.get()
			.uri("/dlc")
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(NOT_FOUND)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.path").isEqualTo("/dlc")
			.jsonPath("$.message").isEqualTo(null);
	}

	@Test
	public void getDlcsInvalidParameter() {

		client.get()
			.uri("/dlc/no-integer")
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(BAD_REQUEST)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.path").isEqualTo("/dlc/no-integer")
			.jsonPath("$.message").isEqualTo("Type mismatch.");
	}

	@Test
	public void getDlcsNotFound() {

		int gameIdNotFound = 200;

		client.get()
			.uri("/dlc/" + gameIdNotFound)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.length()").isEqualTo(0);
	}

	@Test
	public void getDlcsInvalidParameterNegativeValue() {

		int gameIdInvalid = -1;

		client.get()
			.uri("/dlc/" + gameIdInvalid)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.path").isEqualTo("/dlc/-1")
			.jsonPath("$.message").isEqualTo("Invalid gameId: " + gameIdInvalid);
	}
}
