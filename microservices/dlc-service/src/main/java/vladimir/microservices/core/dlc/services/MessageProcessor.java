package vladimir.microservices.core.dlc.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

import vladimir.api.core.dlc.Dlc;
import vladimir.api.core.dlc.DlcService;
import vladimir.api.event.Event;
import vladimir.util.exceptions.EventProcessingException;

@EnableBinding(Sink.class)
public class MessageProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

    private final DlcService dlcService;

    @Autowired
    public MessageProcessor(DlcService dlcService) {
        this.dlcService = dlcService;
    }

    @StreamListener(target = Sink.INPUT)
    public void process(Event<Integer, Dlc> event) {

        LOG.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getType()) {

        case CREATE:
            Dlc dlc = event.getData();
            LOG.info("Create review with ID: {}/{}", dlc.getGameId(), dlc.getDlcId());
            dlcService.createDlc(dlc);
            break;

        case DELETE:
            int gameId = event.getKey();
            LOG.info("Delete dlcs with gameId: {}", gameId);
            dlcService.deleteDlcs(gameId);
            break;

        default:
            String errorMessage = "Incorrect event type: " + event.getType() + ", expected a CREATE or DELETE event";
            LOG.warn(errorMessage);
            throw new EventProcessingException(errorMessage);
        }

        LOG.info("Message processing done!");
    }
}
