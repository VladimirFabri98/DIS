package vladimir.microservices.composite.game.services;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vladimir.api.core.dlc.Dlc;
import vladimir.api.core.dlc.DlcService;
import vladimir.api.core.event.Event;
import vladimir.api.core.event.EventService;
import vladimir.api.core.game.Game;
import vladimir.api.core.game.GameService;
import vladimir.api.core.review.Review;
import vladimir.api.core.review.ReviewService;
import vladimir.util.exceptions.InvalidInputException;
import vladimir.util.exceptions.NotFoundException;
import vladimir.util.http.HttpErrorInfo;
import static reactor.core.publisher.Flux.empty;

@Component
public class GameCompositeIntegration implements GameService, ReviewService, DlcService, EventService {

	private static final Logger LOG = LoggerFactory.getLogger(GameCompositeIntegration.class);

	private final WebClient webClient;
	private final ObjectMapper mapper;

	private final String gameServiceUrl;
	private final String reviewServiceUrl;
	private final String dlcServiceUrl;
	private final String eventServiceUrl;

	@Autowired
	public GameCompositeIntegration(WebClient.Builder webClient, ObjectMapper mapper,
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
	public Flux<Event> getEvents(int gameId) {
		
			String url = eventServiceUrl + "/" + gameId;
			LOG.debug("Will call getEvents API on URL: {}", url);
			return webClient.get().uri(url).retrieve().bodyToFlux(Event.class).log().onErrorResume(error -> empty());

	}

	@Override
	public Game createGame(Game body) {
		return webClient.post().uri(gameServiceUrl + "-post").retrieve().bodyToMono(Game.class).log()
		.onErrorMap(WebClientResponseException.class, ex -> handleException(ex)).block();
	}

	@Override
	public Review createReview(Review body) {
		return webClient.post().uri(reviewServiceUrl + "-post").retrieve().bodyToMono(Review.class).log()
				.onErrorMap(WebClientResponseException.class, ex -> handleException(ex)).block();
	}

	@Override
	public Dlc createDlc(Dlc body) {
		return webClient.post().uri(dlcServiceUrl + "-post").retrieve().bodyToMono(Dlc.class).log()
				.onErrorMap(WebClientResponseException.class, ex -> handleException(ex)).block();
	}

	@Override
	public Event createEvent(Event body) {
		return webClient.post().uri(eventServiceUrl + "-post").retrieve().bodyToMono(Event.class).log()
				.onErrorMap(WebClientResponseException.class, ex -> handleException(ex)).block();
	}

	@Override
	public void deleteGame(int gameId) {
		webClient.delete().uri(gameServiceUrl + "/" + gameId).retrieve().bodyToMono(Game.class).log()
				.onErrorMap(WebClientResponseException.class, ex -> handleException(ex)).block();

	}

	@Override
	public void deleteReviews(int gameId) {
		webClient.delete().uri(reviewServiceUrl + "/" + gameId).retrieve().bodyToMono(Review.class).log()
		.onErrorMap(WebClientResponseException.class, ex -> handleException(ex)).block();

	}

	@Override
	public void deleteDlcs(int gameId) {
		webClient.delete().uri(dlcServiceUrl + "/" + gameId).retrieve().bodyToMono(Game.class).log()
		.onErrorMap(WebClientResponseException.class, ex -> handleException(ex)).block();

	}

	@Override
	public void deleteEvents(int gameId) {
		webClient.delete().uri(eventServiceUrl + "/" + gameId).retrieve().bodyToMono(Game.class).log()
		.onErrorMap(WebClientResponseException.class, ex -> handleException(ex)).block();

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
