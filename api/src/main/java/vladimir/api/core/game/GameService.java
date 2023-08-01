package vladimir.api.core.game;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface GameService {

	@GetMapping("game/{gameId}")
	Game getGame(@PathVariable int gameId);
}
