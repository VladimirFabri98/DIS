package vladimir.api.event;

import java.time.LocalDateTime;

public class Event<K, T> {

	public enum Type {
		CREATE, DELETE
	}

	private Event.Type type;
	private K key;
	private T data;
	private LocalDateTime eventCreatedAt;

	public Event() {
		type = null;
		key = null;
		data = null;
		eventCreatedAt = null;
	}

	public Event(Type type, K key, T data, LocalDateTime eventCreatedAt) {
		this.type = type;
		this.key = key;
		this.data = data;
		this.eventCreatedAt = eventCreatedAt;
	}

	public Event.Type getType() {
		return type;
	}

	public K getKey() {
		return key;
	}

	public T getData() {
		return data;
	}

	public LocalDateTime getEventCreatedAt() {
		return eventCreatedAt;
	}

}
