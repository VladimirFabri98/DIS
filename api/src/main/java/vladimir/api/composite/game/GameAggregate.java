package vladimir.api.composite.game;

import java.util.List;

public class GameAggregate {

	private final int gameId;
	private final String name;
	private final String producer;
	private final int publishYear;
	private final List<ReviewSummary> reviews;
	private final List<DlcSummary> dlcs;
	private final List<EventSummary> events;
	private final ServiceAddresses serviceAddresses;

	public GameAggregate(int gameId, String name, String producer, int publishYear, List<ReviewSummary> reviews,
			List<DlcSummary> dlcs, List<EventSummary> events, ServiceAddresses serviceAddresses) {
		super();
		this.gameId = gameId;
		this.name = name;
		this.producer = producer;
		this.publishYear = publishYear;
		this.reviews = reviews;
		this.dlcs = dlcs;
		this.events = events;
		this.serviceAddresses = serviceAddresses;
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

	public List<ReviewSummary> getReviews() {
		return reviews;
	}

	public List<DlcSummary> getDlcs() {
		return dlcs;
	}

	public List<EventSummary> getEvents() {
		return events;
	}

	public ServiceAddresses getServiceAddresses() {
		return serviceAddresses;
	}

}