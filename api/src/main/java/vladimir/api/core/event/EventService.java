package vladimir.api.core.event;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface EventService {

	@GetMapping("event/{eventId}")
	Event getEvent(@PathVariable int eventId);
}
