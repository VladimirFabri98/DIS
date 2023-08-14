package vladimir.microservices.core.dlc;

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

import vladimir.api.core.dlc.Dlc;
import vladimir.api.event.Event;
import vladimir.microservices.core.dlc.persistence.DlcRepository;
import vladimir.util.exceptions.InvalidInputException;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"spring.data.mongodb.port: 0"})
@AutoConfigureWebTestClient
class DlcServiceApplicationTests {

	@Autowired
	private WebTestClient client;
	
	@Autowired
	private Sink channels;

	private AbstractMessageChannel input = null;
	
	@Autowired
	private DlcRepository repository;
	
	@BeforeEach
	public void setupDb() {
		input = (AbstractMessageChannel) channels.input();
		repository.deleteAll().block();
	}
	
	@Test
	public void getDlcsByGameId() {

		int gameId = 1;

		sendCreateDlcEvent(gameId, 1);
		sendCreateDlcEvent(gameId, 2);
		sendCreateDlcEvent(gameId, 3);

		assertEquals(3, (long)repository.findByGameId(gameId).count().block());

		getAndVerifyDlcByGameId(gameId, OK)
			.jsonPath("$.length()").isEqualTo(3)
			.jsonPath("$[2].gameId").isEqualTo(gameId)
			.jsonPath("$[2].dlcId").isEqualTo(3);
	}

	@Test
	public void getDlcInvalidParameterString() {

        getAndVerifyDlcByGameId("no-integer", BAD_REQUEST)
            .jsonPath("$.path").isEqualTo("/dlc/no-integer")
            .jsonPath("$.message").isEqualTo("Type mismatch.");
	}
	
	@Test
	public void duplicateError() {
		int gameId = 1;
		int dlcId = 1;

		sendCreateDlcEvent(gameId, dlcId);

		assertEquals(1, (long)repository.count().block());

		try {
			sendCreateDlcEvent(gameId, dlcId);
			Assertions.fail("Expected a MessagingException here!");
		} catch (MessagingException me) {
			if (me.getCause() instanceof InvalidInputException)	{
				InvalidInputException iie = (InvalidInputException)me.getCause();
				assertEquals("Duplicate key, Game Id: 1, Dlc Id:1", iie.getMessage());
			} else {
				Assertions.fail("Expected a InvalidInputException as the root cause!");
			}
		}

		assertEquals(1, (long)repository.count().block());
	}
	
	@Test
	public void deleteDlc() {
		int gameId = 1;
		int dlcId = 1;

		sendCreateDlcEvent(gameId, dlcId);
		assertEquals(1, (long)repository.findByGameId(gameId).count().block());

		sendDeleteDlcEvent(gameId);
		assertEquals(0, (long)repository.findByGameId(gameId).count().block());

		sendDeleteDlcEvent(gameId);
	}

	@Test
	public void getDlcNotFound() {

		int dlcIdNotFound = 50;

        getAndVerifyDlcByGameId(dlcIdNotFound, HttpStatus.NOT_FOUND)
            .jsonPath("$.path").isEqualTo("/dlc/" + dlcIdNotFound)
            .jsonPath("$.message").isEqualTo("No dlc found for dlcId: " + dlcIdNotFound);
	}

	@Test
	public void getDlcInvalidParameterNegativeValue() {

        int dlcIdInvalid = -1;

        getAndVerifyDlcByGameId(dlcIdInvalid, UNPROCESSABLE_ENTITY)
            .jsonPath("$.path").isEqualTo("/dlc/" + dlcIdInvalid)
            .jsonPath("$.message").isEqualTo("Invalid dlcId: " + dlcIdInvalid);
	}
	
	private WebTestClient.BodyContentSpec getAndVerifyDlcByGameId(int gameId, HttpStatus expectedStatus) {
		return client.get()
			.uri("/dlc/" + gameId)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody();
	}

	private WebTestClient.BodyContentSpec getAndVerifyDlcByGameId(String gameId, HttpStatus expectedStatus) {
		return client.get()
			.uri("/dlc/" + gameId)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody();
	}

	private void sendCreateDlcEvent(int gameId, int dlcId) {
		Dlc dlc = new Dlc(dlcId, gameId, "name", 20,null);
		
		Event<Integer, Dlc> event = new Event(Event.Type.CREATE, dlcId, dlc, null);
		input.send(new GenericMessage<>(event));
	}

	private void sendDeleteDlcEvent(int dlcId) {
		Event<Integer, Dlc> event = new Event(Event.Type.DELETE, dlcId, null, null);
		input.send(new GenericMessage<>(event));
	}
}