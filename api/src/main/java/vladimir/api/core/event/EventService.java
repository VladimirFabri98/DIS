package vladimir.api.core.event;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import reactor.core.publisher.Flux;

public interface EventService {

	@GetMapping("event/{gameId}")
	Flux<Event> getEvents(@PathVariable int gameId);
	
	@PostMapping(value = "/event-post" , consumes = "application/json")
	Event createEvent(@RequestBody Event body);
	
	@DeleteMapping("event/{gameId}")
	void deleteEvents(@PathVariable int gameId);
}
