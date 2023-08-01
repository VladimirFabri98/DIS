package vladimir.microservices.core.game;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;


@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
class GameServiceApplicationTests {

	@Autowired
	private WebTestClient client;
	
	@Test
	public void getGameById() {

		int gameId = 1;

        client.get()
            .uri("/game/" + gameId)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.gameId").isEqualTo(gameId);
	}

	@Test
	public void getGameInvalidParameterString() {

        client.get()
            .uri("/game/no-integer")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(BAD_REQUEST)
            .expectHeader().contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.path").isEqualTo("/game/no-integer")
            .jsonPath("$.message").isEqualTo("Type mismatch.");
	}

	@Test
	public void getGameNotFound() {

		int gameIdNotFound = 50;

        client.get()
            .uri("/game/" + gameIdNotFound)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound()
            .expectHeader().contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.path").isEqualTo("/game/" + gameIdNotFound)
            .jsonPath("$.message").isEqualTo("No game found for gameId: " + gameIdNotFound);
	}

	@Test
	public void getGameInvalidParameterNegativeValue() {

        int gameIdInvalid = -1;

        client.get()
            .uri("/game/" + gameIdInvalid)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
            .expectHeader().contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.path").isEqualTo("/game/" + gameIdInvalid)
            .jsonPath("$.message").isEqualTo("Invalid gameId: " + gameIdInvalid);
	}

}
