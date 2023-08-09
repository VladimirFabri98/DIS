package vladimir.microservices.core.event.services;

import java.util.List;
import java.util.function.Supplier;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import vladimir.api.core.event.Event;
import vladimir.api.core.event.EventService;
import vladimir.microservices.core.event.persistence.EventEntity;
import vladimir.microservices.core.event.persistence.EventRepository;
import vladimir.util.exceptions.InvalidInputException;
import vladimir.util.http.ServiceUtil;

import static java.util.logging.Level.FINE;

@RestController
public class EventServiceImpl implements EventService {

	private static final Logger LOG = LoggerFactory.getLogger(EventServiceImpl.class);
	private final ServiceUtil serviceUtil;
	private final EventMapperImpl mapper;
	private final EventRepository repository;
	private final Scheduler scheduler;
	
	@Autowired
	public EventServiceImpl(Scheduler scheduler,ServiceUtil serviceUtil, EventMapperImpl mapper, EventRepository repository) {
		this.scheduler = scheduler;
		this.serviceUtil = serviceUtil;
		this.mapper = mapper;
		this.repository = repository;
	}
	
	
	@Override
	public Flux<Event> getEvents(int gameId) {
		if (gameId < 1) throw new InvalidInputException("Invalid gameId: " + gameId);

        LOG.info("Will get events for game with id={}", gameId);

        return asyncFlux(() -> Flux.fromIterable(getByGameId(gameId))).log(null, FINE);
	}

	@Override
	public Event createEvent(Event body) {

        if (body.getGameId() < 1) throw new InvalidInputException("Invalid gameId: " + body.getGameId());

        try {
            EventEntity entity = mapper.apiToEntity(body);
            EventEntity newEntity = repository.save(entity);

            LOG.debug("createEvent: created a event entity: {}/{}", body.getGameId(), body.getEventId());
            return mapper.entityToApi(newEntity);

        } catch (DataIntegrityViolationException dive) {
            throw new InvalidInputException("Duplicate key, Game Id: " + body.getGameId() + ", Event Id:" + body.getEventId());
        }
	}

	@Override
	public void deleteEvents(int gameId) {
		if(gameId < 1) throw new InvalidInputException("Invalid gameId: " + gameId);
		LOG.debug("deleteEvent: tries to delete an entity with gameId: {}", gameId);
		repository.deleteAll(repository.findByGameId(gameId));

	}
	
	protected List<Event> getByGameId(int gameId) {

        List<EventEntity> entityList = repository.findByGameId(gameId);
        List<Event> list = mapper.entityListToApiList(entityList);
        list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

        LOG.debug("getEvents: response size: {}", list.size());

        return list;
    }
	
	private <T> Flux<T> asyncFlux(Supplier<Publisher<T>> publisherSupplier) {
        return Flux.defer(publisherSupplier).subscribeOn(scheduler);
    }
}
