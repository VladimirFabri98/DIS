package vladimir.microservices.core.event.persistence;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "events")
@CompoundIndex(name = "game-event-review-id", unique = true, def = "{'gameId':1, 'eventId':1}")
public class GameEventEntity {

	@Id
	private String id;

	@Version
	private int version;

	private int gameId;
	private int eventId;
	private String type;
	private String name;
	private Date dateOfStart;

	public GameEventEntity() {
		super();
	}

	public GameEventEntity(int gameId, int eventId, String type, String name, Date dateOfStart) {
		super();
		this.gameId = gameId;
		this.eventId = eventId;
		this.type = type;
		this.name = name;
		this.dateOfStart = dateOfStart;
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

	public int getEventId() {
		return eventId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDateOfStart() {
		return dateOfStart;
	}

	public void setDateOfStart(Date dateOfStart) {
		this.dateOfStart = dateOfStart;
	}
	
	public int getVersion() {
		return version;
	}

}
