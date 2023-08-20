package vladimir.api.core.gameEvent;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import reactor.core.publisher.Flux;

public interface GameEventService {

	@GetMapping("game-event/{gameId}")
	Flux<GameEvent> getEvents(@PathVariable int gameId);
	
	@PostMapping(value = "/game-event-post" , consumes = "application/json")
	GameEvent createEvent(@RequestBody GameEvent body);
	
	@DeleteMapping("game-event/{gameId}")
	void deleteEvents(@PathVariable int gameId);
}
