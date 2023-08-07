package vladimir.microservices.core.game.services;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import vladimir.api.core.game.Game;
import vladimir.microservices.core.game.persistence.GameEntity;

@Mapper(componentModel = "spring")
public interface GameMapper {

	@Mappings({
		@Mapping(target = "serviceAddress", ignore = true)
	})
	Game entityToApi(GameEntity entity);
	
	@Mappings({
		@Mapping(target = "id", ignore = true),
		@Mapping(target = "version", ignore = true)
	})
	GameEntity apiToEntity(Game api);
}
