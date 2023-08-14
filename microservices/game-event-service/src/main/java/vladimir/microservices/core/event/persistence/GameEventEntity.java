package vladimir.microservices.core.event.persistence;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Version;


@Entity
@Table(name = "events", indexes = { @Index(name = "events_unique_idx", unique = true, columnList = "gameId,eventId") })
public class GameEventEntity {

	@Id
	@GeneratedValue
	private int id;

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

	public int getId() {
		return id;
	}

	public void setId(int id) {
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
