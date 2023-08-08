package vladimir.api.composite.game;

import java.util.List;

public class GameAggregate {

	private int gameId;
	private String name;
	private String producer;
	private int publishYear;
	private List<ReviewSummary> reviews;
	private List<DlcSummary> dlcs;
	private List<EventSummary> events;
	private ServiceAddresses serviceAddresses;
	
	public GameAggregate() {
		super();
	}

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