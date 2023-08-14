package vladimir.microservices.core.event.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

import vladimir.api.core.gameEvent.GameEvent;
import vladimir.api.core.gameEvent.GameEventService;
import vladimir.api.event.Event;
import vladimir.util.exceptions.EventProcessingException;

@EnableBinding(Sink.class)
public class MessageProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

    private final GameEventService gameService;

    @Autowired
    public MessageProcessor(GameEventService gameService) {
        this.gameService = gameService;
    }

    @StreamListener(target = Sink.INPUT)
    public void process(Event<Integer, GameEvent> event) {

        LOG.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getType()) {

        case CREATE:
            GameEvent gameEvent = event.getData();
            LOG.info("Create gameEvent with ID: {}/{}", gameEvent.getGameId(), gameEvent.getEventId());
            gameService.createEvent(gameEvent);
            break;

        case DELETE:
            int gameId = event.getKey();
            LOG.info("Delete game events with gameId: {}", gameId);
            gameService.deleteEvents(gameId);
            break;

        default:
            String errorMessage = "Incorrect event type: " + event.getType() + ", expected a CREATE or DELETE event";
            LOG.warn(errorMessage);
            throw new EventProcessingException(errorMessage);
        }

        LOG.info("Message processing done!");
    }
}