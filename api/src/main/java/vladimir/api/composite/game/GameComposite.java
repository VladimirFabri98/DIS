package vladimir.api.composite.game;

import java.util.List;

import vladimir.api.core.dlc.Dlc;
import vladimir.api.core.event.Event;
import vladimir.api.core.review.Review;

public class GameComposite {

	private final int gameId;
	private final String name;
	private final String producer;
	private final int publishYear;
	private final List<Review> reviews;
	private final List<Dlc> dlcs;
	private final List<Event> events;

	public GameComposite(int gameId, String name, String producer, int publishYear, List<Review> reviews,
			List<Dlc> dlcs, List<Event> events) {
		super();
		this.gameId = gameId;
		this.name = name;
		this.producer = producer;
		this.publishYear = publishYear;
		this.reviews = reviews;
		this.dlcs = dlcs;
		this.events = events;
	}

	public int getGameId() {
		return gameId;
	}

	public String getName() {
		return name;
	}

	public String getProducer() {
		return producer;
	}

	public int getPublishYear() {
		return publishYear;
	}

	public List<Review> getReviews() {
		return reviews;
	}

	public List<Dlc> getDlcs() {
		return dlcs;
	}

	public List<Event> getEvents() {
		return events;
	}

}
