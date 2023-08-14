package vladimir.microservices.composite.game;

import java.util.concurrent.BlockingQueue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Mono;
import vladimir.api.composite.game.GameAggregate;

import vladimir.microservices.composite.game.services.GameCompositeIntegration;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MessagingTests {

	@Autowired
	WebTestClient client;

	@Autowired
	MessageCollector collector;

	@Autowired
	private GameCompositeIntegration.MessageSources channels;

	BlockingQueue<Message<?>> queueGames = null;
	BlockingQueue<Message<?>> queueReviews = null;
	BlockingQueue<Message<?>> queueDlcs = null;
	BlockingQueue<Message<?>> queueGameEvents = null;

	@BeforeEach
	public void setUp() {
		queueGames = getQueue(channels.outputGames());
		queueReviews = getQueue(channels.outputReviews());
		queueDlcs = getQueue(channels.outputDlcs());
		queueGameEvents = getQueue(channels.outputGameEvents());
	}

	private BlockingQueue<Message<?>> getQueue(MessageChannel messageChannel) {
		return collector.forChannel(messageChannel);
	}

	@Test
	public void createCompositeGame() {

		GameAggregate composite = new GameAggregate(1, "name", "producer", 2020, null, null, null, null);
		postAndVerifyGame(composite, HttpStatus.OK);

		// Assert one expected new game events queued up
		Assertions.assertEquals(1, queueGames.size());
		Assertions.assertEquals(0, queueReviews.size());
		Assertions.assertEquals(0, queueDlcs.size());
		Assertions.assertEquals(0, queueGameEvents.size());

	}

	@Test
	public void deleteCompositeGame() {

		deleteAndVerifyGame(1, HttpStatus.OK);

		// Assert one delete game event queued up
		Assertions.assertEquals(1, queueGames.size());

		// Assert one delete dlc event queued up
		Assertions.assertEquals(1, queueDlcs.size());

		// Assert one delete review event queued up
		Assertions.assertEquals(1, queueReviews.size());

		// Assert one delete gameEvent event queued up
		Assertions.assertEquals(1, queueGameEvents.size());

	}

	private void postAndVerifyGame(GameAggregate compositeGame, HttpStatus expectedStatus) {
		client.post().uri("/game-composite").body(Mono.just(compositeGame), GameAggregate.class).exchange()
				.expectStatus().isEqualTo(expectedStatus);
	}

	private void deleteAndVerifyGame(int gameId, HttpStatus expectedStatus) {
		client.delete().uri("/game-composite/" + gameId).exchange().expectStatus().isEqualTo(expectedStatus);
	}
}
