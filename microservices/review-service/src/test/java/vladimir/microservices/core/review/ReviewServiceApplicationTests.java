package vladimir.microservices.core.review;

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

import vladimir.api.core.review.Review;
import vladimir.microservices.core.review.persistence.ReviewRepository;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"spring.data.mongodb.port: 0"})
@AutoConfigureWebTestClient
class ReviewServiceApplicationTests {

	@Autowired
	private WebTestClient client;
	
	@Autowired
	private ReviewRepository repository;
	
	@Test
	public void getReviewsByGameId() {

		int reviewId = 1;

		client.get()
			.uri("/review/" + reviewId)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.length()").isEqualTo(3)
			.jsonPath("$[0].reviewId").isEqualTo(reviewId);
	}

	@Test
	public void getReviewsMissingParameter() {

		client.get()
			.uri("/review")
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(NOT_FOUND)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.path").isEqualTo("/review")
			.jsonPath("$.message").isEqualTo(null);
	}

	@Test
	public void getReviewsInvalidParameter() {

		client.get()
			.uri("/review/no-integer")
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(BAD_REQUEST)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.path").isEqualTo("/review/no-integer")
			.jsonPath("$.message").isEqualTo("Type mismatch.");
	}

	@Test
	public void getReviewsNotFound() {

		int reviewIdNotFound = 200;

		client.get()
			.uri("/review/" + reviewIdNotFound)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.length()").isEqualTo(0);
	}

	@Test
	public void getReviewsInvalidParameterNegativeValue() {

		int reviewIdInvalid = -1;

		client.get()
			.uri("/review/" + reviewIdInvalid)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.path").isEqualTo("/review/-1")
			.jsonPath("$.message").isEqualTo("Invalid reviewId: " + reviewIdInvalid);
	}
	
	@Test
	public void duplicateError() {

		int gameId = 1;
		int reviewId = 1;

		postAndVerifyReview(gameId, reviewId, OK)
			.jsonPath("$.gameId").isEqualTo(gameId)
			.jsonPath("$.reviewId").isEqualTo(reviewId);

		Assertions.assertEquals(1, repository.count());

		postAndVerifyReview(gameId, reviewId, UNPROCESSABLE_ENTITY)
			.jsonPath("$.path").isEqualTo("/review")
			.jsonPath("$.message").isEqualTo("Duplicate key, Game Id: 1, Review Id:1");

		Assertions.assertEquals(1, repository.count());
	}
	
	@Test
	public void deleteReview() {

		int gameId = 1;
		int reviewId = 1;

		postAndVerifyReview(gameId, reviewId, OK);
		Assertions.assertEquals(1, repository.findByGameId(gameId).size());

		deleteAndVerifyReviewsByGameId(gameId, OK);
		Assertions.assertEquals(0, repository.findByGameId(gameId).size());

		deleteAndVerifyReviewsByGameId(gameId, OK);
	}

	private WebTestClient.BodyContentSpec getAndVerifyReviewsByGameId(String gameId, HttpStatus expectedStatus) {
		return client.get()
			.uri("/review/" + gameId)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody();
	}

	private WebTestClient.BodyContentSpec postAndVerifyReview(int gameId, int reviewId, HttpStatus expectedStatus) {
		Review review = new Review(reviewId, gameId, 5, "SA");
		return client.post()
			.uri("/review")
			.body(just(review), Review.class)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody();
	}

	private WebTestClient.BodyContentSpec deleteAndVerifyReviewsByGameId(int gameId, HttpStatus expectedStatus) {
		return client.delete()
			.uri("/review/" + gameId)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectBody();
	}

}