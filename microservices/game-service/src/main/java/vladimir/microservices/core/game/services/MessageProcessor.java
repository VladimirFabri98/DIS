package vladimir.microservices.core.game.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

import vladimir.api.core.game.Game;
import vladimir.api.core.game.GameService;
import vladimir.api.event.Event;
import vladimir.util.exceptions.EventProcessingException;


@EnableBinding(Sink.class)
public class MessageProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

    private final GameService gameService;

    @Autowired
    public MessageProcessor(GameService gameService) {
        this.gameService = gameService;
    }

    @StreamListener(target = Sink.INPUT)
    public void process(Event<Integer, Game> event) {

        LOG.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getType()) {

        case CREATE:
            Game game = event.getData();
            LOG.info("Create game with ID: {}", game.getGameId());
            Game savedGame = gameService.createGame(game);
            LOG.info("Saved game:" + savedGame.getName() + ", " + savedGame.getProducer() + ", " + savedGame.getReleaseYear());
            break;

        case DELETE:
            int gameId = event.getKey();
            LOG.info("Delete recommendations with GameID: {}", gameId);
            gameService.deleteGame(gameId);
            break;

        default:
            String errorMessage = "Incorrect event type: " + event.getType() + ", expected a CREATE or DELETE event";
            LOG.warn(errorMessage);
            throw new EventProcessingException(errorMessage);
        }

        LOG.info("Message processing done!");
    }
}
