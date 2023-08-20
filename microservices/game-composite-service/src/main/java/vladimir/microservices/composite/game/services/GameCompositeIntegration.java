package vladimir.microservices.composite.game.services;

import static reactor.core.publisher.Flux.empty;
import static vladimir.api.event.Event.Type.CREATE;
import static vladimir.api.event.Event.Type.DELETE;

import java.io.IOException;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

	private final WebClient.Builder webClientBuilder;
	private WebClient webClient;
	private final ObjectMapper mapper;

	
	//Ove vrednosti treba da se poklapaju sa nazivima kontejnera u docker-compose fajlu
	private final String gameServiceUrl = "http://game";
	private final String reviewServiceUrl = "http://review";
	private final String dlcServiceUrl = "http://dlc";
	private final String eventServiceUrl = "http://game-event";
	
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
	public GameCompositeIntegration(WebClient.Builder builder, ObjectMapper mapper, MessageSources messageSources) {

		this.webClientBuilder = builder;
		this.mapper = mapper;
		this.messageSources = messageSources;

	}

	@Override
	public Mono<Game> getGame(int gameId) {

		//http://game/1 ?????
		String url = gameServiceUrl + "/" + gameId;
		LOG.debug("Will call getGame API on URL: {}", url);

		return getWebClient().get().uri(url).retrieve().bodyToMono(Game.class).log();

	}

	@Override
	public Flux<Review> getReviews(int gameId) {
		
			String url = reviewServiceUrl + "/" + gameId;
			LOG.debug("Will call getReviews API on URL: {}", url);
			return getWebClient().get().uri(url).retrieve().bodyToFlux(Review.class).log().onErrorResume(error -> empty());

	}

	@Override
	public Flux<Dlc> getDlcs(int gameId) {
		String url = dlcServiceUrl + "/" + gameId;
		LOG.debug("Will call getDlcs API on URL: {}", url);
		return getWebClient().get().uri(url).retrieve().bodyToFlux(Dlc.class).log().onErrorResume(error -> empty());

	}

	@Override
	public Flux<GameEvent> getEvents(int gameId) {
		
			String url = eventServiceUrl + "/" + gameId;
			LOG.debug("Will call getEvents API on URL: {}", url);
			return getWebClient().get().uri(url).retrieve().bodyToFlux(GameEvent.class).log().onErrorResume(error -> empty());

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
				withPayload(new Event<Integer,Game>(DELETE, gameId, null, LocalDateTime.now())).build());

	}

	@Override
	public void deleteReviews(int gameId) {
		messageSources.outputReviews().send(MessageBuilder.
				withPayload(new Event<Integer,Review>(DELETE, gameId, null, LocalDateTime.now())).build());
	}

	@Override
	public void deleteDlcs(int gameId) {
		messageSources.outputDlcs().send(MessageBuilder.
				withPayload(new Event<Integer,Dlc>(DELETE, gameId, null, LocalDateTime.now())).build());
	}

	@Override
	public void deleteEvents(int gameId) {
		messageSources.outputGameEvents().send(MessageBuilder.
				withPayload(new Event<Integer,GameEvent>(DELETE, gameId, null, LocalDateTime.now())).build());

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
	
	public WebClient getWebClient() {
		if(webClient==null) {
			webClient = webClientBuilder.build();
		}
		return webClient;
	}

}
