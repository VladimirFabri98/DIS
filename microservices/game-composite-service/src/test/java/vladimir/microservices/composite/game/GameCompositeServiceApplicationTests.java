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
import org.springframework.test.web.reactive.server.WebTestClient;

import vladimir.api.core.dlc.Dlc;
import vladimir.api.core.event.Event;
import vladimir.api.core.game.Game;
import vladimir.api.core.review.Review;
import vladimir.microservices.composite.game.services.GameCompositeIntegration;
import vladimir.util.exceptions.InvalidInputException;
import vladimir.util.exceptions.NotFoundException;

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
			thenReturn(new Game(GAME_ID_OK, "name","producer",2015, "mock-address"));
		when(compositeIntegration.getReviews(GAME_ID_OK)).
		thenReturn(Collections.singletonList(new Review(1,GAME_ID_OK,5,"mock-address")));
		when(compositeIntegration.getDlcs(GAME_ID_OK)).
			thenReturn(Collections.singletonList(new Dlc(1,GAME_ID_OK,"name",50,"mock-address")));
		when(compositeIntegration.getEvents(GAME_ID_OK)).
		thenReturn(Collections.singletonList(new Event(1,GAME_ID_OK,"type","name",
				new Date(System.currentTimeMillis()),"mock-addres")));
		

		when(compositeIntegration.getGame(GAME_ID_NOT_FOUND)).thenThrow(new NotFoundException("NOT FOUND: " + GAME_ID_NOT_FOUND));

		when(compositeIntegration.getGame(GAME_ID_INVALID)).thenThrow(new InvalidInputException("INVALID: " + GAME_ID_INVALID));
	}
	
	@Test
	void contextLoads() {
	}
	
	@Test
	public void getProductById() {

        client.get()
            .uri("/game-composite/" + GAME_ID_OK)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.gameId").isEqualTo(GAME_ID_OK)
            .jsonPath("$.reviews.length()").isEqualTo(1)
            .jsonPath("$.dlcs.length()").isEqualTo(1)
        	.jsonPath("$.events.length()").isEqualTo(1);
	}

	@Test
	public void getProductNotFound() {

        client.get()
            .uri("/game-composite/" + GAME_ID_NOT_FOUND)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound()
            .expectHeader().contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.path").isEqualTo("/game-composite/" + GAME_ID_NOT_FOUND)
            .jsonPath("$.message").isEqualTo("NOT FOUND: " + GAME_ID_NOT_FOUND);
	}

	@Test
	public void getProductInvalidInput() {

        client.get()
            .uri("/game-composite/" + GAME_ID_INVALID)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
            .expectHeader().contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.path").isEqualTo("/game-composite/" + GAME_ID_INVALID)
            .jsonPath("$.message").isEqualTo("INVALID: " + GAME_ID_INVALID);
	}

}
