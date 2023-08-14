package vladimir.microservices.core.event.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;

public interface GameEventRepository extends ReactiveCrudRepository<GameEventEntity,String> {

	Flux<GameEventEntity> findByGameId(int gameId);
}
