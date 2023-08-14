package vladimir.microservices.core.event.services;

import java.util.List;

import vladimir.api.core.gameEvent.GameEvent;
import vladimir.microservices.core.event.persistence.GameEventEntity;

//@Mapper(componentModel = "spring")
public interface GameEventMapper {

//	@Mappings({
//		@Mapping(target = "serviceAddress", ignore = true)
//	})
	GameEvent entityToApi(GameEventEntity entity);
	
//	@Mappings({
//		@Mapping(target = "id", ignore = true),
//		@Mapping(target = "version", ignore = true)
//	})
	GameEventEntity apiToEntity(GameEvent api);
	
	List<GameEvent> entityListToApiList(List<GameEventEntity> entity);
    List<GameEventEntity> apiListToEntityList(List<GameEvent> api);
}
