package vladimir.microservices.composite.game.services;

import static reactor.core.publisher.Flux.empty;
import static vladimir.api.event.Event.Type.CREATE;

import java.io.IOException;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vladimir.api.core.dlc.Dlc;
import vladimir.api.core.dlc.DlcService;
import vladimir.api.core.game.Game;
import vladimir.api.core.game.GameService;
import vladimir.api.core.gameEvent.GameEvent;
import vladimir.api.core.gameEvent.GameEventService;
import vladimir.api.core.review.Review;
import vladimir.api.core.review.ReviewService;
import vladimir.api.event.Event;
import vladimir.util.exceptions.InvalidInputException;
import vladimir.util.exceptions.NotFoundException;
import vladimir.util.http.HttpErrorInfo;

@EnableBinding(GameCompositeIntegration.MessageSources.class)
@Component
public class GameCompositeIntegration implements GameService, ReviewService, DlcService, GameEventService {

	private static final Logger LOG = LoggerFactory.getLogger(GameCompositeIntegration.class);

	private final WebClient webClient;
	private final ObjectMapper mapper;

	private final String gameServiceUrl;
	private final String reviewServiceUrl;
	private final String dlcServiceUrl;
	private final String eventServiceUrl;
	
	private MessageSources messageSources;
	
	public interface MessageSources {
		
		String OUTPUT_GAMES = "output-games";
		String OUTPUT_REVIEWS = "output-reviews";
		String OUTPUT_DLCS = "output-dlcs";
		String OUTPUT_GAMEEVENTS = "output-game-events";
		
		@Output(OUTPUT_GAMES)
		MessageChannel outputGames();
		
		@Output(OUTPUT_REVIEWS)
		MessageChannel outputReviews();
		
		@Output(OUTPUT_DLCS)
		MessageChannel outputDlcs();
		
		@Output(OUTPUT_GAMEEVENTS)
		MessageChannel outputGameEvents();
		
	}

	@Autowired
	public GameCompositeIntegration(WebClient.Builder webClient, ObjectMapper mapper, MessageSources messageSources,
			@Value("${app.game-service.host}") String gameServiceHost,
			@Value("${app.game-service.port}") int gameServicePort,

			@Value("${app.dlc-service.host}") String dlcServiceHost,
			@Value("${app.dlc-service.port}") int dlcServicePort,

			@Value("${app.review-service.host}") String reviewServiceHost,
			@Value("${app.review-service.port}") int reviewServicePort,

			@Value("${app.event-service.host}") String eventServiceHost,
			@Value("${app.event-service.port}") int eventServicePort) {

		this.webClient = webClient.build();
		this.mapper = mapper;
		this.messageSources = messageSources;

		gameServiceUrl = "http://" + gameServiceHost + ":" + gameServicePort + "/game";
		dlcServiceUrl = "http://" + dlcServiceHost + ":" + dlcServicePort + "/dlc";
		reviewServiceUrl = "http://" + reviewServiceHost + ":" + reviewServicePort + "/review";
		eventServiceUrl = "http://" + eventServiceHost + ":" + eventServicePort + "/event";

	}

	@Override
	public Mono<Game> getGame(int gameId) {

		String url = gameServiceUrl + "/" + gameId;
		LOG.debug("Will call getGame API on URL: {}", url);

		return webClient.get().uri(url).retrieve().bodyToMono(Game.class).log()
				.onErrorMap(WebClientResponseException.class, ex -> handleException(ex));

	}

	@Override
	public Flux<Review> getReviews(int gameId) {
		
			String url = reviewServiceUrl + "/" + gameId;
			LOG.debug("Will call getReviews API on URL: {}", url);
			return webClient.get().uri(url).retrieve().bodyToFlux(Review.class).log().onErrorResume(error -> empty());

	}

	@Override
	public Flux<Dlc> getDlcs(int gameId) {
		String url = dlcServiceUrl + "/" + gameId;
		LOG.debug("Will call getDlcs API on URL: {}", url);
		return webClient.get().uri(url).retrieve().bodyToFlux(Dlc.class).log().onErrorResume(error -> empty());

	}

	@Override
	public Flux<GameEvent> getEvents(int gameId) {
		
			String url = eventServiceUrl + "/" + gameId;
			LOG.debug("Will call getEvents API on URL: {}", url);
			return webClient.get().uri(url).retrieve().bodyToFlux(GameEvent.class).log().onErrorResume(error -> empty());

	}

	@Override
	public Game createGame(Game body) {
		messageSources.outputGames().send(MessageBuilder.
				withPayload(new Event<Integer,Game>(CREATE, body.getGameId(), body, LocalDateTime.now())).build());
        return body;
	}

	@Override
	public Review createReview(Review body) {
		messageSources.outputReviews().send(MessageBuilder.
				withPayload(new Event<Integer,Review>(CREATE, body.getGameId(), body, LocalDateTime.now())).build());
        return body;
	}

	@Override
	public Dlc createDlc(Dlc body) {
		messageSources.outputDlcs().send(MessageBuilder.
				withPayload(new Event<Integer,Dlc>(CREATE, body.getGameId(), body, LocalDateTime.now())).build());
        return body;
	}

	@Override
	public GameEvent createEvent(GameEvent body) {
		messageSources.outputGameEvents().send(MessageBuilder.
				withPayload(new Event<Integer,GameEvent>(CREATE, body.getGameId(), body, LocalDateTime.now())).build());
        return body;
	}

	@Override
	public void deleteGame(int gameId) {
		messageSources.outputGames().send(MessageBuilder.
				withPayload(new Event<Integer,Game>(CREATE, gameId, null, LocalDateTime.now())).build());

	}

	@Override
	public void deleteReviews(int gameId) {
		messageSources.outputReviews().send(MessageBuilder.
				withPayload(new Event<Integer,Review>(CREATE, gameId, null, LocalDateTime.now())).build());
	}

	@Override
	public void deleteDlcs(int gameId) {
		messageSources.outputDlcs().send(MessageBuilder.
				withPayload(new Event<Integer,Dlc>(CREATE, gameId, null, LocalDateTime.now())).build());
	}

	@Override
	public void deleteEvents(int gameId) {
		messageSources.outputGameEvents().send(MessageBuilder.
				withPayload(new Event<Integer,GameEvent>(CREATE, gameId, null, LocalDateTime.now())).build());

	}
	
	//Health checking methods
	
	public Mono<Health> getGameHealth() {
        return getHealth(gameServiceUrl);
    }

    public Mono<Health> getDlcHealth() {
        return getHealth(dlcServiceUrl);
    }

    public Mono<Health> getReviewHealth() {
        return getHealth(reviewServiceUrl);
    }
    
    public Mono<Health> getGameEventHealth() {
        return getHealth(eventServiceUrl);
    }

    private Mono<Health> getHealth(String url) {
        url += "/actuator/health";
        LOG.debug("Will call the Health API on URL: {}", url);
        return webClient.get().uri(url).retrieve().bodyToMono(String.class)
            .map(s -> new Health.Builder().up().build())
            .onErrorResume(ex -> Mono.just(new Health.Builder().down(ex).build()))
            .log();
    }

	// Error handling methods
	private String getErrorMessage(WebClientResponseException ex) {
		try {
			return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
		} catch (IOException ioex) {
			return ex.getMessage();
		}
	}
	
	private Throwable handleException(Throwable ex) {

        if (!(ex instanceof WebClientResponseException)) {
            LOG.warn("Got a unexpected error: {}, will rethrow it", ex.toString());
            return ex;
        }
        
        WebClientResponseException wcre = (WebClientResponseException)ex;

        switch (wcre.getStatusCode()) {

        case NOT_FOUND:
            return new NotFoundException(getErrorMessage(wcre));

        case UNPROCESSABLE_ENTITY :
            return new InvalidInputException(getErrorMessage(wcre));

        default:
            LOG.warn("Got a unexpected HTTP error: {}, will rethrow it", wcre.getStatusCode());
            LOG.warn("Error body: {}", wcre.getResponseBodyAsString());
            return ex;
        }
	}

}
