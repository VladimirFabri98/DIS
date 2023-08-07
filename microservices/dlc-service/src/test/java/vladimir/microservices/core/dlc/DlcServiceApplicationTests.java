package vladimir.microservices.core.dlc;

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

import vladimir.api.core.dlc.Dlc;
import vladimir.microservices.core.dlc.persistence.DlcRepository;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"spring.data.mongodb.port: 0"})
@AutoConfigureWebTestClient
class DlcServiceApplicationTests {

	@Autowired
	private WebTestClient client;
	
	@Autowired
	private DlcRepository repository;
	
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
	
	@Test
	public void duplicateError() {

		int gameId = 1;
		int dlcId = 1;

		postAndVerifyDlc(gameId, dlcId, OK)
			.jsonPath("$.gameId").isEqualTo(gameId)
			.jsonPath("$.dlcId").isEqualTo(dlcId);

		Assertions.assertEquals(1, repository.count());

		postAndVerifyDlc(gameId, dlcId, UNPROCESSABLE_ENTITY)
			.jsonPath("$.path").isEqualTo("/dlc")
			.jsonPath("$.message").isEqualTo("Duplicate key, Game Id: 1, Dlc Id:1");

		Assertions.assertEquals(1, repository.count());
	}
	
	@Test
	public void deleteDlc() {

		int gameId = 1;
		int dlcId = 1;

		postAndVerifyDlc(gameId, dlcId, OK);
		Assertions.assertEquals(1, repository.findByGameId(gameId).size());

		deleteAndVerifyDlcsByGameId(gameId, OK);
		Assertions.assertEquals(0, repository.findByGameId(gameId).size());

		deleteAndVerifyDlcsByGameId(gameId, OK);
	}

	private WebTestClient.BodyContentSpec getAndVerifyDlcsByGameId(String gameId, HttpStatus expectedStatus) {
		return client.get()
			.uri("/dlc/" + gameId)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody();
	}

	private WebTestClient.BodyContentSpec postAndVerifyDlc(int gameId, int dlcId, HttpStatus expectedStatus) {
		Dlc dlc = new Dlc(dlcId,gameId,"n",20,"SA");
		return client.post()
			.uri("/dlc")
			.body(just(dlc), Dlc.class)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody();
	}

	private WebTestClient.BodyContentSpec deleteAndVerifyDlcsByGameId(int gameId, HttpStatus expectedStatus) {
		return client.delete()
			.uri("/dlc/" + gameId)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectBody();
	}
}
