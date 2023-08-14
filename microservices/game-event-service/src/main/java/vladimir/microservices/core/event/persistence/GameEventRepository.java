package vladimir.microservices.core.event.persistence;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface GameEventRepository extends CrudRepository<GameEventEntity, Integer> {

	@Transactional(readOnly = true)
	List<GameEventEntity> findByGameId(int gameId);
}
