package vladimir.microservices.core.event;

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
class EventServiceApplicationTests {

	@Autowired
	private WebTestClient client;
	
	@Test
	public void getEventsByGameId() {

		int gameId = 1;

		client.get()
			.uri("/event/" + gameId)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.length()").isEqualTo(2)
			.jsonPath("$[0].gameId").isEqualTo(gameId);
	}

	@Test
	public void getEventsMissingParameter() {

		client.get()
			.uri("/event")
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(NOT_FOUND)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.path").isEqualTo("/event")
			.jsonPath("$.message").isEqualTo(null);
	}

	@Test
	public void getEventsInvalidParameter() {

		client.get()
			.uri("/event/no-integer")
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(BAD_REQUEST)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.path").isEqualTo("/event/no-integer")
			.jsonPath("$.message").isEqualTo("Type mismatch.");
	}

	@Test
	public void getEventsNotFound() {

		int gameIdNotFound = 200;

		client.get()
			.uri("/event/" + gameIdNotFound)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.length()").isEqualTo(0);
	}

	@Test
	public void getEventsInvalidParameterNegativeValue() {

		int gameIdInvalid = -1;

		client.get()
			.uri("/event/" + gameIdInvalid)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.path").isEqualTo("/event/-1")
			.jsonPath("$.message").isEqualTo("Invalid gameId: " + gameIdInvalid);
	}

}
