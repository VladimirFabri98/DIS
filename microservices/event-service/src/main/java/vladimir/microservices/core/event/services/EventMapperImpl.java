package vladimir.microservices.core.event.services;

import java.util.ArrayList;
import java.util.List;
import vladimir.api.core.event.Event;
import vladimir.microservices.core.event.persistence.EventEntity;
import org.springframework.stereotype.Component;

@Component
public class EventMapperImpl implements EventMapper
{
    public Event entityToApi(final EventEntity entity) {
        if (entity == null) {
            return null;
        }
        final Event event = new Event();
        event.setEventId(entity.getEventId());
        event.setGameId(entity.getGameId());
        event.setType(entity.getType());
        event.setName(entity.getName());
        event.setDateOfStart(entity.getDateOfStart());
        return event;
    }
    
    public EventEntity apiToEntity(final Event api) {
        if (api == null) {
            return null;
        }
        final EventEntity eventEntity = new EventEntity();
        eventEntity.setGameId(api.getGameId());
        eventEntity.setEventId(api.getEventId());
        eventEntity.setType(api.getType());
        eventEntity.setName(api.getName());
        eventEntity.setDateOfStart(api.getDateOfStart());
        return eventEntity;
    }
    
    public List<Event> entityListToApiList(final List<EventEntity> entity) {
        if (entity == null) {
            return null;
        }
        final List<Event> list = new ArrayList<Event>(entity.size());
        for (final EventEntity eventEntity : entity) {
            list.add(this.entityToApi(eventEntity));
        }
        return list;
    }
    
    public List<EventEntity> apiListToEntityList(final List<Event> api) {
        if (api == null) {
            return null;
        }
        final List<EventEntity> list = new ArrayList<EventEntity>(api.size());
        for (final Event event : api) {
            list.add(this.apiToEntity(event));
        }
        return list;
    }
}