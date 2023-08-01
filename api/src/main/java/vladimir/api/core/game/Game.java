package vladimir.api.core.game;

public class Game {

	private int gameId;
	private String name;
	private String producer;
	private int releaseYear;
	private final String serviceYear;

	public Game(int gameId, String name, String producer, int releaseYear, String serviceYear) {
		super();
		this.gameId = gameId;
		this.name = name;
		this.producer = producer;
		this.releaseYear = releaseYear;
		this.serviceYear = serviceYear;
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

	public int getPublishYear() {
		return releaseYear;
	}

	public void setPublishYear(int releaseYear) {
		this.releaseYear = releaseYear;
	}

	public String getServiceYear() {
		return serviceYear;
	}

}
