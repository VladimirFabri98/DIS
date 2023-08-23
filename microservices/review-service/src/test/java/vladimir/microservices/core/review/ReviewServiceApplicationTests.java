package vladimir.microservices.core.review;

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

import vladimir.api.core.review.Review;
import vladimir.api.event.Event;
import vladimir.microservices.core.review.persistence.ReviewRepository;
import vladimir.util.exceptions.InvalidInputException;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"spring.data.mongodb.port: 0", "eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
@AutoConfigureWebTestClient
class ReviewServiceApplicationTests {

	@Autowired
	private WebTestClient client;
	
	@Autowired
	private Sink channels;

	private AbstractMessageChannel input = null;
	
	@Autowired
	private ReviewRepository repository;
	
	@BeforeEach
	public void setupDb() {
		input = (AbstractMessageChannel) channels.input();
		repository.deleteAll().block();
	}
	
	@Test
	public void getReviewsByGameId() {

		int gameId = 1;

		sendCreateReviewEvent(gameId, 1);
		sendCreateReviewEvent(gameId, 2);
		sendCreateReviewEvent(gameId, 3);

		assertEquals(3, (long)repository.findByGameId(gameId).count().block());

		getAndVerifyReviewByGameId(gameId, OK)
			.jsonPath("$.length()").isEqualTo(3)
			.jsonPath("$[2].gameId").isEqualTo(gameId)
			.jsonPath("$[2].reviewId").isEqualTo(3);
	}

	@Test
	public void getReviewInvalidParameterString() {

        getAndVerifyReviewByGameId("no-integer", BAD_REQUEST)
            .jsonPath("$.path").isEqualTo("/review/no-integer")
            .jsonPath("$.message").isEqualTo("Type mismatch.");
	}
	
	@Test
	public void duplicateError() {
		int gameId = 1;
		int reviewId = 1;

		sendCreateReviewEvent(gameId, reviewId);

		assertEquals(1, (long)repository.count().block());

		try {
			sendCreateReviewEvent(gameId, reviewId);
			Assertions.fail("Expected a MessagingException here!");
		} catch (MessagingException me) {
			if (me.getCause() instanceof InvalidInputException)	{
				InvalidInputException iie = (InvalidInputException)me.getCause();
				assertEquals("Duplicate key, Game Id: 1, Review Id:1", iie.getMessage());
			} else {
				Assertions.fail("Expected a InvalidInputException as the root cause!");
			}
		}

		assertEquals(1, (long)repository.count().block());
	}
	
	@Test
	public void deleteReview() {
		int gameId = 1;
		int reviewId = 1;

		sendCreateReviewEvent(gameId, reviewId);
		assertEquals(1, (long)repository.findByGameId(gameId).count().block());

		sendDeleteReviewEvent(gameId);
		assertEquals(0, (long)repository.findByGameId(gameId).count().block());

		sendDeleteReviewEvent(gameId);
	}

	@Test
	public void getReviewNotFound() {

		int reviewIdNotFound = 50;

        getAndVerifyReviewByGameId(reviewIdNotFound, HttpStatus.NOT_FOUND)
            .jsonPath("$.path").isEqualTo("/review/" + reviewIdNotFound)
            .jsonPath("$.message").isEqualTo("No review found for reviewId: " + reviewIdNotFound);
	}

	@Test
	public void getReviewInvalidParameterNegativeValue() {

        int reviewIdInvalid = -1;

        getAndVerifyReviewByGameId(reviewIdInvalid, UNPROCESSABLE_ENTITY)
            .jsonPath("$.path").isEqualTo("/review/" + reviewIdInvalid)
            .jsonPath("$.message").isEqualTo("Invalid reviewId: " + reviewIdInvalid);
	}
	
	private WebTestClient.BodyContentSpec getAndVerifyReviewByGameId(int gameId, HttpStatus expectedStatus) {
		return client.get()
			.uri("/review/" + gameId)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody();
	}

	private WebTestClient.BodyContentSpec getAndVerifyReviewByGameId(String gameId, HttpStatus expectedStatus) {
		return client.get()
			.uri("/review/" + gameId)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody();
	}

	private void sendCreateReviewEvent(int gameId, int reviewId) {
		Review review = new Review(reviewId, gameId, 5, null);
		
		Event<Integer, Review> event = new Event(Event.Type.CREATE, reviewId, review, null);
		input.send(new GenericMessage<>(event));
	}

	private void sendDeleteReviewEvent(int reviewId) {
		Event<Integer, Review> event = new Event(Event.Type.DELETE, reviewId, null, null);
		input.send(new GenericMessage<>(event));
	}

}