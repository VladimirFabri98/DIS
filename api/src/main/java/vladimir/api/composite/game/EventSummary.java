package vladimir.api.composite.game;

import java.util.Date;

public class EventSummary {

	private int eventId;
	private String type;
	private String name;
	private Date dateOfStart;
	
	public EventSummary() {
		super();
	}

	public EventSummary(int eventId, String type, String name, Date dateOfStart) {
		super();
		this.eventId = eventId;
		this.type = type;
		this.name = name;
		this.dateOfStart = dateOfStart;
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

}
