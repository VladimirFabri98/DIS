package vladimir.microservices.core.game.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

@Repository
public interface GameRepository extends ReactiveCrudRepository<GameEntity,String> {

	Mono<GameEntity> findByGameId(int gameId);
}
