package vladimir.microservices.composite.game.services;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.handler.advice.RequestHandlerCircuitBreakerAdvice.CircuitBreakerOpenException;
import org.springframework.web.bind.annotation.RestController;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.reactor.retry.RetryOperator;

import reactor.core.publisher.Mono;
import vladimir.api.composite.game.DlcSummary;
import vladimir.api.composite.game.EventSummary;
import vladimir.api.composite.game.GameAggregate;
import vladimir.api.composite.game.GameCompositeService;
import vladimir.api.composite.game.ReviewSummary;
import vladimir.api.composite.game.ServiceAddresses;
import vladimir.api.core.dlc.Dlc;
import vladimir.api.core.game.Game;
import vladimir.api.core.gameEvent.GameEvent;
import vladimir.api.core.review.Review;
import vladimir.util.http.ServiceUtil;

@RestController
public class GameCompositeServiceImpl implements GameCompositeService {

	private final Logger LOG = LoggerFactory.getLogger(GameCompositeServiceImpl.class);
	private final ServiceUtil serviceUtil;
	private final GameCompositeIntegration integration;

	@Autowired
	public GameCompositeServiceImpl(ServiceUtil serviceUtil, GameCompositeIntegration integration) {
		this.serviceUtil = serviceUtil;
		this.integration = integration;
	}

	@Override
	public Mono<GameAggregate> getCompositeGame(int gameCompositeId, int delay, int faultPercent) {
		return Mono.zip(
				values -> createGameAggregate((Game) values[0], (List<Review>) values[1], (List<Dlc>) values[2],(List<GameEvent>) values[3], serviceUtil.getServiceAddress()),
				integration.getGame(gameCompositeId, delay, faultPercent)
				.onErrorReturn(CallNotPermittedException.class, getGameFallbackValue(gameCompositeId)),
				integration.getReviews(gameCompositeId).collectList(),
				integration.getDlcs(gameCompositeId).collectList(),
				integration.getEvents(gameCompositeId).collectList())
		.doOnError(ex -> LOG.warn("getCompositeGame failed: {}", ex.toString()))
		.log();
		
	}

	private GameAggregate createGameAggregate(Game game, List<Review> reviews, List<Dlc> dlcs, List<GameEvent> events,
			String serviceAddress) {

		// 1. Setup game info
		int gameId = game.getGameId();
		String name = game.getName();
		String producer = game.getProducer();
		int releaseYear = game.getReleaseYear();

		// 2. Copy summary review info, if available
		List<ReviewSummary> reviewSummaries = (reviews == null) ? null
				: reviews.stream().map(r -> new ReviewSummary(r.getReviewId(), r.getRating()))
						.collect(Collectors.toList());

		// 3. Copy summary dlc info, if available
		List<DlcSummary> dlcSummaries = (dlcs == null) ? null
				: dlcs.stream().map(r -> new DlcSummary(r.getDlcId(), r.getName(), r.getPrice()))
						.collect(Collectors.toList());

		// 4. Copy summary event info, if available
		List<EventSummary> eventSummaries = (events == null) ? null
				: events.stream()
						.map(r -> new EventSummary(r.getEventId(), r.getType(), r.getName(), r.getDateOfStart()))
						.collect(Collectors.toList());

		// 4. Create info regarding the involved microservices addresses
		String gameAddress = game.getServiceAddress();
		String reviewAddress = (reviews != null && reviews.size() > 0) ? reviews.get(0).getServiceAddress() : "";
		String dlcAddress = (dlcs != null && dlcs.size() > 0) ? dlcs.get(0).getServiceAddress() : "";
		String eventAddress = (events != null && events.size() > 0) ? events.get(0).getServiceAddress() : "";
		ServiceAddresses serviceAddresses = new ServiceAddresses(serviceAddress, gameAddress, reviewAddress, dlcAddress,
				eventAddress);

		return new GameAggregate(gameId, name, producer, releaseYear, reviewSummaries, dlcSummaries, eventSummaries,
				serviceAddresses);

	}

	@Override
	public void createCompositeGame(GameAggregate body) {
		try {
			integration.createGame(
					new Game(body.getGameId(), body.getName(), body.getProducer(), body.getPublishYear(), null));
			if (body.getReviews() != null) {
				body.getReviews().forEach(r -> {
					integration.createReview(new Review(r.getReviewId(), body.getGameId(), r.getRating(), null));
				});
			}
			if (body.getDlcs() != null) {
				body.getDlcs().forEach(d -> {
					integration.createDlc(new Dlc(d.getDlcId(), body.getGameId(), d.getName(), d.getPrice(), null));
				});
			}
			if (body.getEvents() != null) {
				body.getEvents().forEach(e -> {
					integration.createEvent(new GameEvent(e.getEventId(), body.getGameId(), e.getType(), e.getName(),
							e.getDateOfStart(), null));
				});
			}
		} catch (RuntimeException e) {
			LOG.warn("createCompositeGame failed", e);
			throw e;
		}
	}

	@Override
	public void deleteCompositeGame(int gameId) {
		integration.deleteGame(gameId);
		integration.deleteReviews(gameId);
		integration.deleteDlcs(gameId);
		integration.deleteEvents(gameId);
	}
	
	private Game getGameFallbackValue(int gameId) {

        LOG.warn("Creating a fallback game for gameId = {}", gameId);

        return new Game(gameId,"name","producer",1961,serviceUtil.getServiceAddress());
    }

}
