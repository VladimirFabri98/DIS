package vladimir.api.core.event;

import java.sql.Date;

public class Event {

	private int eventId;
	private int gameId;
	private String type;
	private String name;
	private Date dateOfStart;
	private final String serviceAdress;

	public Event(int eventId, int gameId, String type, String name, Date dateOfStart, String serviceAdress) {
		super();
		this.eventId = eventId;
		this.gameId = gameId;
		this.type = type;
		this.name = name;
		this.dateOfStart = dateOfStart;
		this.serviceAdress = serviceAdress;
	}

	public int getEventId() {
		return eventId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
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

	public String getServiceAdress() {
		return serviceAdress;
	}

}
