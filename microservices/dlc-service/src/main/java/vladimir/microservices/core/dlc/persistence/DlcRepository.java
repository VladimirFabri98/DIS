package vladimir.microservices.core.dlc.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;

public interface DlcRepository extends ReactiveCrudRepository<DlcEntity,String>{
	
	Flux<DlcEntity> findByGameId(int gameId);
	
}
