package vladimir.api.core.game;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import reactor.core.publisher.Mono;

public interface GameService {

	@GetMapping("game/{gameId}")
	Mono<Game> getGame(@PathVariable int gameId);
	
	@PostMapping(value = "/game-post" , consumes = "application/json")
	Game createGame(@RequestBody Game body);
	
	@DeleteMapping("game/{gameId}")
	void deleteGame(@PathVariable int gameId);
}
