package vladimir.microservices.core.event;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import vladimir.api.core.event.Event;
import vladimir.microservices.core.event.persistence.EventRepository;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {
"spring.datasource.url=jdbc:h2:mem:event-db"})
@AutoConfigureWebTestClient
class EventServiceApplicationTests {

	@Autowired
	private WebTestClient client;
	
	@Autowired
	private EventRepository repository;
	
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
	
	@Test
	public void duplicateError() {

		int gameId = 1;
		int eventId = 1;

		postAndVerifyEvent(gameId, eventId, OK)
			.jsonPath("$.gameId").isEqualTo(gameId)
			.jsonPath("$.eventId").isEqualTo(eventId);

		Assertions.assertEquals(1, repository.count());

		postAndVerifyEvent(gameId, eventId, UNPROCESSABLE_ENTITY)
			.jsonPath("$.path").isEqualTo("/event")
			.jsonPath("$.message").isEqualTo("Duplicate key, Game Id: 1, Event Id:1");

		Assertions.assertEquals(1, repository.count());
	}
	
	@Test
	public void deleteEvent() {

		int gameId = 1;
		int eventId = 1;

		postAndVerifyEvent(gameId, eventId, OK);
		Assertions.assertEquals(1, repository.findByGameId(gameId).size());

		deleteAndVerifyEventsByGameId(gameId, OK);
		Assertions.assertEquals(0, repository.findByGameId(gameId).size());

		deleteAndVerifyEventsByGameId(gameId, OK);
	}

	private WebTestClient.BodyContentSpec getAndVerifyEventsByGameId(String gameId, HttpStatus expectedStatus) {
		return client.get()
			.uri("/event/" + gameId)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody();
	}

	private WebTestClient.BodyContentSpec postAndVerifyEvent(int gameId, int eventId, HttpStatus expectedStatus) {
		Event event = new Event(eventId, gameId,"t","n",null,"SA");
		return client.post()
			.uri("/event")
			.body(just(event), Event.class)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody();
	}

	private WebTestClient.BodyContentSpec deleteAndVerifyEventsByGameId(int gameId, HttpStatus expectedStatus) {
		return client.delete()
			.uri("/event/" + gameId)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectBody();
	}

}