package vladimir.microservices.core.review.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;

public interface ReviewRepository extends ReactiveCrudRepository<ReviewEntity,String> {

	Flux<ReviewEntity> findByGameId(int gameId);
}
