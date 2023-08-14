package vladimir.microservices.core.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.AbstractMessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.web.reactive.server.WebTestClient;

import vladimir.api.core.gameEvent.GameEvent;
import vladimir.api.event.Event;
import vladimir.microservices.core.event.persistence.GameEventRepository;
import vladimir.util.exceptions.InvalidInputException;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"spring.data.mongodb.port: 0"})
@AutoConfigureWebTestClient
class GameEventServiceApplicationTests {

	@Autowired
	private WebTestClient client;
	
	@Autowired
	private Sink channels;

	private AbstractMessageChannel input = null;
	
	@Autowired
	private GameEventRepository repository;
	
	@BeforeEach
	public void setupDb() {
		input = (AbstractMessageChannel) channels.input();
		repository.deleteAll();
	}
	
	@Test
	public void getGameEventsByGameId() {

		int gameId = 1;

		sendCreateGameEventEvent(gameId, 1);
		sendCreateGameEventEvent(gameId, 2);
		sendCreateGameEventEvent(gameId, 3);

		assertEquals(3, (long)repository.findByGameId(gameId).count().block());

		getAndVerifyGameEventByGameId(gameId, OK)
			.jsonPath("$.length()").isEqualTo(3)
			.jsonPath("$[2].gameId").isEqualTo(gameId)
			.jsonPath("$[2].gameEventId").isEqualTo(3);
	}

	@Test
	public void getGameEventInvalidParameterString() {

        getAndVerifyGameEventByGameId("no-integer", BAD_REQUEST)
            .jsonPath("$.path").isEqualTo("/gameEvent/no-integer")
            .jsonPath("$.message").isEqualTo("Type mismatch.");
	}
	
	@Test
	public void duplicateError() {
		int gameId = 1;
		int gameEventId = 1;

		sendCreateGameEventEvent(gameId, gameEventId);

		assertEquals(1, (long)repository.count().block());

		try {
			sendCreateGameEventEvent(gameId, gameEventId);
			Assertions.fail("Expected a MessagingException here!");
		} catch (MessagingException me) {
			if (me.getCause() instanceof InvalidInputException)	{
				InvalidInputException iie = (InvalidInputException)me.getCause();
				assertEquals("Duplicate key, Game Id: 1, GameEvent Id:1", iie.getMessage());
			} else {
				Assertions.fail("Expected a InvalidInputException as the root cause!");
			}
		}

		assertEquals(1, (long)repository.count().block());
	}
	
	@Test
	public void deleteGameEvent() {
		int gameId = 1;
		int gameEventId = 1;

		sendCreateGameEventEvent(gameId, gameEventId);
		assertEquals(1, (long)repository.findByGameId(gameId).count().block());

		sendDeleteGameEventEvent(gameId);
		assertEquals(0, (long)repository.findByGameId(gameId).count().block());

		sendDeleteGameEventEvent(gameId);
	}

	@Test
	public void getGameEventNotFound() {

		int gameEventIdNotFound = 50;

        getAndVerifyGameEventByGameId(gameEventIdNotFound, HttpStatus.NOT_FOUND)
            .jsonPath("$.path").isEqualTo("/event/" + gameEventIdNotFound)
            .jsonPath("$.message").isEqualTo("No gameEvent found for gameEventId: " + gameEventIdNotFound);
	}

	@Test
	public void getGameEventInvalidParameterNegativeValue() {

        int gameEventIdInvalid = -1;

        getAndVerifyGameEventByGameId(gameEventIdInvalid, UNPROCESSABLE_ENTITY)
            .jsonPath("$.path").isEqualTo("/event/" + gameEventIdInvalid)
            .jsonPath("$.message").isEqualTo("Invalid gameEventId: " + gameEventIdInvalid);
	}
	
	private WebTestClient.BodyContentSpec getAndVerifyGameEventByGameId(int gameId, HttpStatus expectedStatus) {
		return client.get()
			.uri("/event/" + gameId)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody();
	}

	private WebTestClient.BodyContentSpec getAndVerifyGameEventByGameId(String gameId, HttpStatus expectedStatus) {
		return client.get()
			.uri("/event/" + gameId)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody();
	}

	private void sendCreateGameEventEvent(int gameId, int gameEventId) {
		GameEvent gameEvent = new GameEvent(gameEventId, gameId, "type", "name", null,null);
		
		Event<Integer, GameEvent> event = new Event(Event.Type.CREATE, gameEventId, gameEvent, null);
		input.send(new GenericMessage<>(event));
	}

	private void sendDeleteGameEventEvent(int gameEventId) {
		Event<Integer, GameEvent> event = new Event(Event.Type.DELETE, gameEventId, null, null);
		input.send(new GenericMessage<>(event));
	}
}