package vladimir.microservices.core.game;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.test.StepVerifier;
import vladimir.api.core.game.Game;
import vladimir.microservices.core.game.persistence.GameRepository;

@SpringBootTest(webEnvironment = RANDOM_PORT,properties = {"spring.data.mongodb.port: 0"})
@AutoConfigureWebTestClient
class GameServiceApplicationTests {

	@Autowired
	private WebTestClient client;
	
	@Autowired
	private GameRepository repository;
	
	@BeforeEach
	public void setupDb() {
		repository.deleteAll();
	}
	
	@Test
	public void getGameById() {

		int gameId = 1;

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

		postAndVerifyGame(gameId, OK);
		StepVerifier.create(repository.findByGameId(gameId)).expectNextMatches(e -> e != null).verifyComplete();

		postAndVerifyGame(gameId, UNPROCESSABLE_ENTITY)
			.jsonPath("$.path").isEqualTo("/game")
			.jsonPath("$.message").isEqualTo("Duplicate key, Game Id: " + gameId);
	}
	
	@Test
	public void deleteGame() {

		int gameId = 1;

		postAndVerifyGame(gameId, OK);
		StepVerifier.create(repository.findByGameId(gameId)).expectNextMatches(e -> e != null).verifyComplete();

		deleteAndVerifyGame(gameId, OK);
		StepVerifier.create(repository.findByGameId(gameId)).expectNextMatches(e -> e == null).verifyComplete();

		deleteAndVerifyGame(gameId, OK);
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

	private WebTestClient.BodyContentSpec postAndVerifyGame(int gameId, HttpStatus expectedStatus) {
		Game game = new Game(gameId,"n","p",2020,"SA");
		return client.post()
			.uri("/game")
			.body(just(game), Game.class)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody();
	}

	private WebTestClient.BodyContentSpec deleteAndVerifyGame(int gameId, HttpStatus expectedStatus) {
		return client.delete()
			.uri("/game/" + gameId)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectBody();
	}

}