package vladimir.microservices.core.game.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface GameRepository extends ReactiveCrudRepository<GameEntity,String> {

	Mono<GameEntity> findByGameId(int gameId);
}
