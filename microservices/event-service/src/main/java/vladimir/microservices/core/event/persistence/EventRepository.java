package vladimir.microservices.core.event.persistence;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface EventRepository extends CrudRepository<EventEntity, Integer> {

	@Transactional(readOnly = true)
	List<EventEntity> findByGameId(int gameId);
}
