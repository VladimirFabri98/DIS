package vladimir.api.core.game;

public class Game {

	private int gameId;
	private String name;
	private String producer;
	private int releaseYear;
	private String serviceAddress;

	public Game() {
		this.gameId = 0;
		this.name = null;
		this.producer = null;
		this.releaseYear = 0;
		this.serviceAddress = null;
	}
	
	public Game(int gameId, String name, String producer, int releaseYear, String serviceAddress) {
		super();
		this.gameId = gameId;
		this.name = name;
		this.producer = producer;
		this.releaseYear = releaseYear;
		this.serviceAddress = serviceAddress;
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProducer() {
		return producer;
	}

	public void setProducer(String producer) {
		this.producer = producer;
	}

	public int getReleaseYear() {
		return releaseYear;
	}

	public void setReleaseYear(int releaseYear) {
		this.releaseYear = releaseYear;
	}

	public String getServiceAddress() {
		return serviceAddress;
	}
	
	public void setServiceAddress(String serviceAddress) {
		this.serviceAddress = serviceAddress;
	}

}
