package vladimir.microservices.core.game.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "games")
public class GameEntity {

	@Id
	private String id;

	@Version
	private Integer version;

	@Indexed(unique = true)
	private int gameId;
	
	private String name;
	private String producer;
	private int releaseYear;

	public GameEntity() {
		super();
	}

	public GameEntity(int gameId, String name, String producer, int releaseYear) {
		super();
		this.gameId = gameId;
		this.name = name;
		this.producer = producer;
		this.releaseYear = releaseYear;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Integer getVersion() {
		return version;
	}

}
