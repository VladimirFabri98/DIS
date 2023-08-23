package vladimir.microservices.core.game;

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

import vladimir.api.core.game.Game;
import vladimir.api.event.Event;
import vladimir.microservices.core.game.persistence.GameRepository;
import vladimir.util.exceptions.InvalidInputException;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = RANDOM_PORT,properties = {"spring.data.mongodb.port: 0", "eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
@AutoConfigureWebTestClient
class GameServiceApplicationTests {

	@Autowired
	private WebTestClient client;
	
	@Autowired
	private Sink channels;

	private AbstractMessageChannel input = null;
	
	@Autowired
	private GameRepository repository;
	
	@BeforeEach
	public void setupDb() {
		input = (AbstractMessageChannel) channels.input();
		repository.deleteAll().block();
	}
	
	@Test
	public void getGameById() {

		int gameId = 1;

		assertNull(repository.findByGameId(gameId).block());
		assertEquals(0, (long)repository.count().block());

		sendCreateGameEvent(gameId);

		assertNotNull(repository.findByGameId(gameId).block());
		assertEquals(1, (long)repository.count().block());

		getAndVerifyGame(gameId, OK)
            .jsonPath("$.gameId").isEqualTo(gameId);
	}

	@Test
	public void getGameInvalidParameterString() {

        getAndVerifyGame("no-integer", BAD_REQUEST)
            .jsonPath("$.path").isEqualTo("/game/no-integer")
            .jsonPath("$.message").isEqualTo("Type mismatch.");
	}
	
	@Test
	public void duplicateError() {

		int gameId = 1;

		assertNull(repository.findByGameId(gameId).block());

		sendCreateGameEvent(gameId);

		assertNotNull(repository.findByGameId(gameId).block());

		try {
			sendCreateGameEvent(gameId);
			Assertions.fail("Expected a MessagingException here!");
		} catch (MessagingException me) {
			if (me.getCause() instanceof InvalidInputException)	{
				InvalidInputException iie = (InvalidInputException)me.getCause();
				assertEquals("Duplicate key, Game Id: " + gameId, iie.getMessage());
			} else {
				Assertions.fail("Expected a InvalidInputException as the root cause!");
			}
		}
	}
	
	@Test
	public void deleteGame() {

		int gameId = 1;

		sendCreateGameEvent(gameId);
		assertNotNull(repository.findByGameId(gameId).block());

		sendDeleteGameEvent(gameId);
		assertNull(repository.findByGameId(gameId).block());

		sendDeleteGameEvent(gameId);
	}

	@Test
	public void getGameNotFound() {

		int gameIdNotFound = 50;

        getAndVerifyGame(gameIdNotFound, HttpStatus.NOT_FOUND)
            .jsonPath("$.path").isEqualTo("/game/" + gameIdNotFound)
            .jsonPath("$.message").isEqualTo("No game found for gameId: " + gameIdNotFound);
	}

	@Test
	public void getGameInvalidParameterNegativeValue() {

        int gameIdInvalid = -1;

        getAndVerifyGame(gameIdInvalid, UNPROCESSABLE_ENTITY)
            .jsonPath("$.path").isEqualTo("/game/" + gameIdInvalid)
            .jsonPath("$.message").isEqualTo("Invalid gameId: " + gameIdInvalid);
	}
	
	private WebTestClient.BodyContentSpec getAndVerifyGame(int gameId, HttpStatus expectedStatus) {
		return client.get()
			.uri("/game/" + gameId)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody();
	}

	private WebTestClient.BodyContentSpec getAndVerifyGame(String gameId, HttpStatus expectedStatus) {
		return client.get()
			.uri("/game/" + gameId)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody();
	}

	private void sendCreateGameEvent(int gameId) {
		Game game = new Game(gameId, "name", "producer", 2020,null);
		
		Event<Integer, Game> event = new Event(Event.Type.CREATE, gameId, game, null);
		input.send(new GenericMessage<>(event));
	}

	private void sendDeleteGameEvent(int gameId) {
		Event<Integer, Game> event = new Event(Event.Type.DELETE, gameId, null, null);
		input.send(new GenericMessage<>(event));
	}

}