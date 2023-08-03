package vladimir.microservices.composite.game.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import vladimir.api.composite.game.DlcSummary;
import vladimir.api.composite.game.EventSummary;
import vladimir.api.composite.game.GameAggregate;
import vladimir.api.composite.game.GameCompositeService;
import vladimir.api.composite.game.ReviewSummary;
import vladimir.api.composite.game.ServiceAddresses;
import vladimir.api.core.dlc.Dlc;
import vladimir.api.core.event.Event;
import vladimir.api.core.game.Game;
import vladimir.api.core.review.Review;
import vladimir.util.http.ServiceUtil;

@RestController
public class GameCompositeServiceImpl implements GameCompositeService {

	private final ServiceUtil serviceUtil;
	private final GameCompositeIntegration integration;

	@Autowired
	public GameCompositeServiceImpl(ServiceUtil serviceUtil, GameCompositeIntegration integration) {
		this.serviceUtil = serviceUtil;
		this.integration = integration;
	}

	@Override
	public GameAggregate getAggregate(int gameCompositeId) {
		Game game = integration.getGame(gameCompositeId);
		List<Review> reviews = integration.getReviews(gameCompositeId);
		List<Dlc> dlcs = integration.getDlcs(gameCompositeId);
		List<Event> events = integration.getEvents(gameCompositeId);

		return createGameAggregate(game, reviews, dlcs, events, serviceUtil.getServiceAddress());
	}

	private GameAggregate createGameAggregate(Game game, List<Review> reviews, List<Dlc> dlcs, List<Event> events,
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
		ServiceAddresses serviceAddresses = new ServiceAddresses(serviceAddress,gameAddress,reviewAddress,dlcAddress,eventAddress);
		
		
		return new GameAggregate(gameId, name, producer, releaseYear, reviewSummaries, dlcSummaries, eventSummaries,
				serviceAddresses);

	}

}