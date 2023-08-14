package vladimir.microservices.core.event.services;

import static reactor.core.publisher.Mono.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vladimir.api.core.gameEvent.GameEvent;
import vladimir.api.core.gameEvent.GameEventService;
import vladimir.microservices.core.event.persistence.GameEventEntity;
import vladimir.microservices.core.event.persistence.GameEventRepository;
import vladimir.util.exceptions.InvalidInputException;
import vladimir.util.exceptions.NotFoundException;
import vladimir.util.http.ServiceUtil;

@RestController
public class GameEventServiceImpl implements GameEventService {

	private static final Logger LOG = LoggerFactory.getLogger(GameEventServiceImpl.class);
	private final ServiceUtil serviceUtil;
	private final GameEventMapperImpl mapper;
	private final GameEventRepository repository;

	@Autowired
	public GameEventServiceImpl(ServiceUtil serviceUtil, GameEventMapperImpl mapper, GameEventRepository repository) {
		this.serviceUtil = serviceUtil;
		this.mapper = mapper;
		this.repository = repository;
	}

	@Override
	public Flux<GameEvent> getEvents(int gameId) {
		if (gameId < 1)
			throw new InvalidInputException("Invalid gameId: " + gameId);

		return repository.findByGameId(gameId)
				.switchIfEmpty(error(new NotFoundException("No gameEvents found for gameId: " + gameId))).log()
				.map(e -> mapper.entityToApi(e))
				.map(e -> {
					e.setServiceAddress(serviceUtil.getServiceAddress());
					return e;
				});
	}

	@Override
	public GameEvent createEvent(GameEvent body) {
		if (body.getGameId() < 1)
			throw new InvalidInputException("Invalid gameId: " + body.getGameId());

		GameEventEntity entity = mapper.apiToEntity(body);
		Mono<GameEvent> newEntity = repository.save(entity).log()
				.onErrorMap(DuplicateKeyException.class,
						ex -> new InvalidInputException(
								"Duplicate key, Game Id: " + body.getGameId() + ", GameEvent Id:" + body.getEventId()))
				.map(e -> mapper.entityToApi(e));

		return newEntity.block();
	}

	@Override
	public void deleteEvents(int gameId) {
		if (gameId < 1)
			throw new InvalidInputException("Invalid gameId: " + gameId);
		LOG.debug("deleteEvent: tries to delete an entity with gameId: {}", gameId);
		repository.deleteAll(repository.findByGameId(gameId));

	}

}
