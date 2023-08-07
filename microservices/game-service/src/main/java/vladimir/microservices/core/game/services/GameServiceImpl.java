package vladimir.microservices.core.game.services;

/*import org.slf4j.Logger;
import org.slf4j.LoggerFactory;*/
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;

import vladimir.api.core.game.Game;
import vladimir.api.core.game.GameService;
import vladimir.microservices.core.game.persistence.GameEntity;
import vladimir.microservices.core.game.persistence.GameRepository;
import vladimir.util.exceptions.InvalidInputException;
import vladimir.util.exceptions.NotFoundException;
import vladimir.util.http.ServiceUtil;

@RestController
public class GameServiceImpl implements GameService {

	//private static final Logger LOG = LoggerFactory.getLogger(GameServiceImpl.class);
	private final ServiceUtil serviceUtil;
	private final GameRepository repository;
	private final GameMapper mapper;

	@Autowired
	public GameServiceImpl(ServiceUtil serviceUtil, GameRepository repository,  GameMapper mapper) {
		this.serviceUtil = serviceUtil;
		this.repository = repository;
		this.mapper = mapper;
	}

	@Override
	public Game getGame(int gameId) {
        if (gameId < 1) throw new InvalidInputException("Invalid gameId: " + gameId);
        
        GameEntity entity = repository.findByGameId(gameId).orElseThrow( ()-> new NotFoundException("No game found for gameId: " + gameId));
        
        Game response = mapper.entityToApi(entity);
        
        response.setServiceAddress(serviceUtil.getServiceAddress());
        
        return response;
	}

	@Override
	public Game createGame(Game body) {
		try {
			GameEntity entity = mapper.apiToEntity(body);
			GameEntity newEntity = repository.save(entity);
			return mapper.entityToApi(newEntity);
		} catch (DuplicateKeyException e) {
			throw new InvalidInputException("Duplicate key, gameId: " + body.getGameId());
		}
	}

	@Override
	public void deleteGame(int gameId) {
		repository.findByGameId(gameId).ifPresent(e -> repository.delete(e));

	}

}
