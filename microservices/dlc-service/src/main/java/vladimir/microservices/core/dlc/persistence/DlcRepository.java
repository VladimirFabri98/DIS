package vladimir.microservices.core.dlc.persistence;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface DlcRepository extends CrudRepository<DlcEntity,String>{
	
	List<DlcEntity> findByGameId(int gameId);
	
}
