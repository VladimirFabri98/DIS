package vladimir.microservices.core.game.services;

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
	private final GameMapper mapper;
	private final GameRepository repository;
	
	@Autowired
	public GameServiceImpl(ServiceUtil serviceUtil, GameMapper mapper, GameRepository repository) {
		this.serviceUtil = serviceUtil;
		this.mapper = mapper;
		this.repository = repository;
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
