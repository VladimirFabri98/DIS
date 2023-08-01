package vladimir.api.composite.game;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface GameCompositeService {

	@GetMapping("game-composite/{gameCompositeId}")
	GameAggregate getAggregate(@PathVariable int gameCompositeId);
}
