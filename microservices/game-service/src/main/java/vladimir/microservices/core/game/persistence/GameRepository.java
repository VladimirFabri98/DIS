package vladimir.microservices.core.game.persistence;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface GameRepository extends PagingAndSortingRepository<GameEntity,String> {

	Optional<GameEntity> findByGameId(int gameId);
}
