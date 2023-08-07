package vladimir.microservices.core.event.services;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import vladimir.api.core.event.Event;
import vladimir.microservices.core.event.persistence.EventEntity;

@Mapper
public interface EventMapper {

	@Mappings({
		@Mapping(target = "serviceAddress", ignore = true)
	})
	Event entityToApi(EventEntity entity);
	
	@Mappings({
		@Mapping(target = "id", ignore = true),
		@Mapping(target = "version", ignore = true)
	})
	EventEntity apiToEntity(Event api);
	
	List<Event> entityListToApiList(List<EventEntity> entity);
    List<EventEntity> apiListToEntityList(List<Event> api);
	
}