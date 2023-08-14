package vladimir.microservices.core.event.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import vladimir.api.core.gameEvent.GameEvent;
import vladimir.microservices.core.event.persistence.GameEventEntity;

@Component
public class GameEventMapperImpl implements GameEventMapper
{
    public GameEvent entityToApi(final GameEventEntity entity) {
        if (entity == null) {
            return null;
        }
        final GameEvent event = new GameEvent();
        event.setEventId(entity.getEventId());
        event.setGameId(entity.getGameId());
        event.setType(entity.getType());
        event.setName(entity.getName());
        event.setDateOfStart(entity.getDateOfStart());
        return event;
    }
    
    public GameEventEntity apiToEntity(final GameEvent api) {
        if (api == null) {
            return null;
        }
        final GameEventEntity eventEntity = new GameEventEntity();
        eventEntity.setGameId(api.getGameId());
        eventEntity.setEventId(api.getEventId());
        eventEntity.setType(api.getType());
        eventEntity.setName(api.getName());
        eventEntity.setDateOfStart(api.getDateOfStart());
        return eventEntity;
    }
    
    public List<GameEvent> entityListToApiList(final List<GameEventEntity> entity) {
        if (entity == null) {
            return null;
        }
        final List<GameEvent> list = new ArrayList<GameEvent>(entity.size());
        for (final GameEventEntity eventEntity : entity) {
            list.add(this.entityToApi(eventEntity));
        }
        return list;
    }
    
    public List<GameEventEntity> apiListToEntityList(final List<GameEvent> api) {
        if (api == null) {
            return null;
        }
        final List<GameEventEntity> list = new ArrayList<GameEventEntity>(api.size());
        for (final GameEvent event : api) {
            list.add(this.apiToEntity(event));
        }
        return list;
    }
}