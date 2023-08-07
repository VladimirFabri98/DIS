package vladimir.microservices.core.game.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import vladimir.api.core.game.Game;
import vladimir.api.core.game.GameService;
import vladimir.util.exceptions.InvalidInputException;
import vladimir.util.exceptions.NotFoundException;
import vladimir.util.http.ServiceUtil;

@RestController
public class GameServiceImpl implements GameService {

	private static final Logger LOG = LoggerFactory.getLogger(GameServiceImpl.class);
	private final ServiceUtil serviceUtil;
	
	@Autowired
	public GameServiceImpl(ServiceUtil serviceUtil) {
		this.serviceUtil = serviceUtil;
	}
	
	@Override
	public Game getGame(int gameId) {
		LOG.debug("/Game return the found game for gameId={}", gameId);

        if (gameId < 1) throw new InvalidInputException("Invalid gameId: " + gameId);
        
        if(gameId == 50) throw new NotFoundException("No game found for gameId: " + gameId);
		
		return new Game(gameId,"World of Warcraft","Blizzard",2007,serviceUtil.getServiceAddress());
	}

	@Override
	public Game createGame(Game body) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteGame(int gameId) {
		// TODO Auto-generated method stub
		
	}

}
