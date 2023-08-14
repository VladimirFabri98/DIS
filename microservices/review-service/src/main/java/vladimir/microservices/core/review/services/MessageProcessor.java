package vladimir.microservices.core.review.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

import vladimir.api.core.review.Review;
import vladimir.api.core.review.ReviewService;
import vladimir.api.event.Event;
import vladimir.microservices.core.review.services.MessageProcessor;
import vladimir.util.exceptions.EventProcessingException;

@EnableBinding(Sink.class)
public class MessageProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

    private final ReviewService reviewService;

    @Autowired
    public MessageProcessor(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @StreamListener(target = Sink.INPUT)
    public void process(Event<Integer, Review> event) {

        LOG.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getType()) {

        case CREATE:
            Review review = event.getData();
            LOG.info("Create review with ID: {}/{}", review.getGameId(), review.getReviewId());
            reviewService.createReview(review);
            break;

        case DELETE:
            int gameId = event.getKey();
            LOG.info("Delete reviews with gameId: {}", gameId);
            reviewService.deleteReviews(gameId);
            break;

        default:
            String errorMessage = "Incorrect event type: " + event.getType() + ", expected a CREATE or DELETE event";
            LOG.warn(errorMessage);
            throw new EventProcessingException(errorMessage);
        }

        LOG.info("Message processing done!");
    }
}

