package vladimir.microservices.core.game.services;

import static reactor.core.publisher.Mono.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import vladimir.api.core.game.Game;
import vladimir.api.core.game.GameService;
import vladimir.microservices.core.game.persistence.GameEntity;
import vladimir.microservices.core.game.persistence.GameRepository;
import vladimir.util.exceptions.InvalidInputException;
import vladimir.util.exceptions.NotFoundException;
import vladimir.util.http.ServiceUtil;

@RestController
public class GameServiceImpl implements GameService {

	private static final Logger LOG = LoggerFactory.getLogger(GameServiceImpl.class);
	private final ServiceUtil serviceUtil;
	private final GameMapperImpl mapper;
	private final GameRepository repository;
	
	@Autowired
	public GameServiceImpl(ServiceUtil serviceUtil, GameMapperImpl mapper, GameRepository repository) {
		this.serviceUtil = serviceUtil;
		this.mapper = mapper;
		this.repository = repository;
	}
	
	@Override
	public Mono<Game> getGame(int gameId) {
		if (gameId < 1) throw new InvalidInputException("Invalid gameId: " + gameId);

        return repository.findByGameId(gameId)
            .switchIfEmpty(error(new NotFoundException("No game found for gameId: " + gameId)))
            .log()
            .map(e -> mapper.entityToApi(e))
            .map(e -> {e.setServiceAddress(serviceUtil.getServiceAddress()); return e;});
	}

	@Override
	public Game createGame(Game body) {
		if (body.getGameId() < 1) throw new InvalidInputException("Invalid gameId: " + body.getGameId());

        GameEntity entity = mapper.apiToEntity(body);
        Mono<Game> newEntity = repository.save(entity)
            .log()
            .onErrorMap(
                DuplicateKeyException.class,
                ex -> new InvalidInputException("Duplicate key, Game Id: " + body.getGameId()))
            .map(e -> mapper.entityToApi(e));

        return newEntity.block();
	}

	@Override
	public void deleteGame(int gameId) {
		if(gameId < 1) throw new InvalidInputException("Invalid gameId: " + gameId);
		LOG.debug("deleteGame: tries to delete an entity with gameId: {}", gameId);
		repository.findByGameId(gameId).log().map(e -> repository.delete(e)).flatMap(e -> e).block();

	}

}
