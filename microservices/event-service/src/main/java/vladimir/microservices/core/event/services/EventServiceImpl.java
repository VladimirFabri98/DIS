package vladimir.microservices.core.event.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;

import vladimir.api.core.event.Event;
import vladimir.api.core.event.EventService;
import vladimir.microservices.core.event.persistence.EventEntity;
import vladimir.microservices.core.event.persistence.EventRepository;
import vladimir.util.exceptions.InvalidInputException;
import vladimir.util.http.ServiceUtil;

@RestController
public class EventServiceImpl implements EventService {

	private static final Logger LOG = LoggerFactory.getLogger(EventServiceImpl.class);
	private final ServiceUtil serviceUtil;
	private final EventMapper mapper;
	private final EventRepository repository;
	
	@Autowired
	public EventServiceImpl(ServiceUtil serviceUtil, EventMapper mapper, EventRepository repository ) {
		this.serviceUtil = serviceUtil;
		this.mapper = mapper;
		this.repository = repository;
	}

	@Override
	public List<Event> getEvents(int gameId) {
        if (gameId < 1) throw new InvalidInputException("Invalid gameId: " + gameId);
        
        List<EventEntity> listEntity = repository.findByGameId(gameId);
        List<Event> listApi = mapper.entityListToApiList(listEntity);
        listApi.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));
        
        
        LOG.debug("/event response size: {}", listApi.size());
        
        return listApi;
	}

	@Override
	public Event createEvent(Event body) {
		try {
			EventEntity entity = mapper.apiToEntity(body);
			EventEntity newEntity = repository.save(entity);
			Event response =  mapper.entityToApi(newEntity);
			
			LOG.debug("createEvent: created a event entity: {}/{}", body.getGameId(),body.getEventId());
			
			return response;
		} catch (DuplicateKeyException e) {
			throw new InvalidInputException("Duplicate key, gameId: "+ body.getGameId() + ", eventId: " + body.getEventId());
		}
		
	}

	@Override
	public void deleteEvents(int gameId) {
		LOG.debug("deleteEvents: tries to delete events for the game with gameId: {}", gameId);
		repository.deleteAll(repository.findByGameId(gameId));
		
	}
}
