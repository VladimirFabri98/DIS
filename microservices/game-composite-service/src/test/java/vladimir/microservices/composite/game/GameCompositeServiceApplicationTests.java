package vladimir.microservices.composite.game;

import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.sql.Date;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import vladimir.api.composite.game.DlcSummary;
import vladimir.api.composite.game.EventSummary;
import vladimir.api.composite.game.GameAggregate;
import vladimir.api.composite.game.ReviewSummary;
import vladimir.api.core.dlc.Dlc;
import vladimir.api.core.event.Event;
import vladimir.api.core.game.Game;
import vladimir.api.core.review.Review;
import vladimir.microservices.composite.game.services.GameCompositeIntegration;
import vladimir.util.exceptions.InvalidInputException;
import vladimir.util.exceptions.NotFoundException;
import static reactor.core.publisher.Mono.just;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class GameCompositeServiceApplicationTests {

	private static final int GAME_ID_OK = 1;
	private static final int GAME_ID_NOT_FOUND = 2;
	private static final int GAME_ID_INVALID = 3;

	@Autowired
	private WebTestClient client;

	@MockBean
	private GameCompositeIntegration compositeIntegration;

	@BeforeEach
	public void setUp() {

		when(compositeIntegration.getGame(GAME_ID_OK)).
			thenReturn(just(new Game(GAME_ID_OK, "name","producer",2015, "mock-address")));
		when(compositeIntegration.getReviews(GAME_ID_OK)).
		thenReturn(Flux.fromIterable(Collections.singletonList(new Review(1,GAME_ID_OK,5,"mock-address"))));
		when(compositeIntegration.getDlcs(GAME_ID_OK)).
			thenReturn(Flux.fromIterable(Collections.singletonList(new Dlc(1,GAME_ID_OK,"name",50,"mock-address"))));
		when(compositeIntegration.getEvents(GAME_ID_OK)).
		thenReturn(Flux.fromIterable(Collections.singletonList(new Event(1,GAME_ID_OK,"type","name",
				new Date(System.currentTimeMillis()),"mock-addres"))));
		

		when(compositeIntegration.getGame(GAME_ID_NOT_FOUND)).thenThrow(new NotFoundException("NOT FOUND: " + GAME_ID_NOT_FOUND));

		when(compositeIntegration.getGame(GAME_ID_INVALID)).thenThrow(new InvalidInputException("INVALID: " + GAME_ID_INVALID));
	}

	@Test
	void contextLoads() {
	}

	@Test
	public void getGameById() {

		getAndVerifyGame(GAME_ID_OK, HttpStatus.OK).jsonPath("$.gameId").isEqualTo(GAME_ID_OK)
				.jsonPath("$.dlcs.length()").isEqualTo(1).jsonPath("$.reviews.length()").isEqualTo(1)
				.jsonPath("$.events.length()").isEqualTo(1);
	}

	@Test
	public void getGameNotFound() {

		getAndVerifyGame(GAME_ID_NOT_FOUND, HttpStatus.NOT_FOUND).jsonPath("$.path")
				.isEqualTo("/game-composite/" + GAME_ID_NOT_FOUND).jsonPath("$.message")
				.isEqualTo("NOT FOUND: " + GAME_ID_NOT_FOUND);
	}

	@Test
	public void getGameInvalidInput() {

		getAndVerifyGame(GAME_ID_INVALID, UNPROCESSABLE_ENTITY)
				.jsonPath("$.path").isEqualTo("/game-composite/" + GAME_ID_INVALID).jsonPath("$.message")
				.isEqualTo("INVALID: " + GAME_ID_INVALID);
	}

	@Test
	public void createCompositeGame1() {
		GameAggregate compositeGame = new GameAggregate(1, "n", "p", 2000, null, null, null, null);
		postAndVerifyGame(compositeGame, HttpStatus.OK);
	}

	@Test
	public void createCompositeGame2() {
		GameAggregate compositeGame = new GameAggregate(1, "n", "p", 2000,
				Collections.singletonList(new ReviewSummary(1, 4)),
				Collections.singletonList(new DlcSummary(1, "n", 50)),
				Collections.singletonList(new EventSummary(1, "t", "n", null)), null);
		postAndVerifyGame(compositeGame, HttpStatus.OK);
	}
	
	@Test
	public void deleteCompositeGame() {
		GameAggregate compositeGame = new GameAggregate(1, "n", "p", 2000,
				Collections.singletonList(new ReviewSummary(1, 4)),
				Collections.singletonList(new DlcSummary(1, "n", 50)),
				Collections.singletonList(new EventSummary(1, "t", "n", null)), null);

		postAndVerifyGame(compositeGame, HttpStatus.OK);

		deleteAndVerifyGame(compositeGame.getGameId(), HttpStatus.OK);
		deleteAndVerifyGame(compositeGame.getGameId(), HttpStatus.OK);
	}

	private WebTestClient.BodyContentSpec getAndVerifyGame(int gameId, HttpStatus expectedStatus) {
		return client.get().uri("/game-composite/" + gameId).accept(APPLICATION_JSON).exchange().expectStatus()
				.isEqualTo(expectedStatus).expectHeader().contentType(APPLICATION_JSON).expectBody();
	}

	private void postAndVerifyGame(GameAggregate compositeGame, HttpStatus expectedStatus) {
		client.post().uri("/game-composite").body(just(compositeGame), GameAggregate.class).exchange().expectStatus()
				.isEqualTo(expectedStatus);
	}

	private void deleteAndVerifyGame(int gameId, HttpStatus expectedStatus) {
		client.delete().uri("/game-composite/" + gameId).exchange().expectStatus().isEqualTo(expectedStatus);
	}

}
