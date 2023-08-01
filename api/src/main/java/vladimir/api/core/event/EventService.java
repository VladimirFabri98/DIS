package vladimir.api.core.event;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface EventService {

	@GetMapping("event/{gameId}")
	List<Event> getEvents(@PathVariable int gameId);
}
