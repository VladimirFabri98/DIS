package vladimir.microservices.core.game.services;

import vladimir.api.core.game.Game;
import vladimir.microservices.core.game.persistence.GameEntity;
import org.springframework.stereotype.Component;

@Component
public class GameMapperImpl implements GameMapper
{
    public Game entityToApi(final GameEntity entity) {
        if (entity == null) {
            return null;
        }
        final Game game = new Game();
        game.setGameId(entity.getGameId());
        game.setName(entity.getName());
        game.setProducer(entity.getProducer());
        game.setReleaseYear(entity.getReleaseYear());
        return game;
    }
    
    public GameEntity apiToEntity(final Game api) {
        if (api == null) {
            return null;
        }
        final GameEntity gameEntity = new GameEntity();
        gameEntity.setGameId(api.getGameId());
        gameEntity.setName(api.getName());
        gameEntity.setProducer(api.getProducer());
        gameEntity.setReleaseYear(api.getReleaseYear());
        return gameEntity;
    }
}
