package vladimir.microservices.composite.game.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

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

@Component
public class GameCompositeIntegration implements GameService, ReviewService, DlcService, EventService {

	private static final Logger LOG = LoggerFactory.getLogger(GameCompositeIntegration.class);

	private final RestTemplate restTemplate;
	private final ObjectMapper mapper;

	private final String gameServiceUrl;
	private final String reviewServiceUrl;
	private final String dlcServiceUrl;
	private final String eventServiceUrl;

	@Autowired
	public GameCompositeIntegration(RestTemplate restTemplate, ObjectMapper mapper,
			@Value("${app.game-service.host}") String gameServiceHost,
			@Value("${app.game-service.port}") int gameServicePort,

			@Value("${app.dlc-service.host}") String dlcServiceHost,
			@Value("${app.dlc-service.port}") int dlcServicePort,

			@Value("${app.review-service.host}") String reviewServiceHost,
			@Value("${app.review-service.port}") int reviewServicePort,

			@Value("${app.event-service.host}") String eventServiceHost,
			@Value("${app.event-service.port}") int eventServicePort) {

		this.restTemplate = restTemplate;
		this.mapper = mapper;

		gameServiceUrl = "http://" + gameServiceHost + ":" + gameServicePort + "/game/";
		dlcServiceUrl = "http://" + dlcServiceHost + ":" + dlcServicePort + "/dlc/";
		reviewServiceUrl = "http://" + reviewServiceHost + ":" + reviewServicePort + "/review/";
		eventServiceUrl = "http://" + eventServiceHost + ":" + eventServicePort + "/event/";

	}

	@Override
	public Game getGame(int gameId) {
		try {
			String url = gameServiceUrl + gameId;
			LOG.debug("Will call getGame API on URL: {}", url);
			Game game = restTemplate.getForObject(url, Game.class);
			LOG.debug("Found a game with id: {}", game.getGameId());

			return game;

		} catch (HttpClientErrorException ex) {
			switch (ex.getStatusCode()) {

			case NOT_FOUND:
				throw new NotFoundException(getErrorMessage(ex));

			case UNPROCESSABLE_ENTITY:
				throw new InvalidInputException(getErrorMessage(ex));

			default:
				LOG.warn("Got a unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
				LOG.warn("Error body: {}", ex.getResponseBodyAsString());
				throw ex;
			}
		}
	}

	private String getErrorMessage(HttpClientErrorException ex) {
		try {
			return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
		} catch (IOException ioex) {
			return ex.getMessage();
		}
	}

	@Override
	public List<Review> getReviews(int gameId) {
		try {
			String url = reviewServiceUrl + gameId;
			LOG.debug("Will call getReviews API on URL: {}", url);
			List<Review> reviews = restTemplate
					.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Review>>() {
					}).getBody();
			LOG.debug("Found {} reviews for a game with id: {}", reviews.size(), gameId);
			
			return reviews;
			
		} catch (Exception ex) {
			LOG.warn("Got an exception while requesting reviews, return zero reviews: {}", ex.getMessage());
			return new ArrayList<>();
		}

	}

	@Override
	public List<Dlc> getDlcs(int gameId) {
		try {
			String url = dlcServiceUrl + gameId;
			LOG.debug("Will call getDlcs API on URL: {}", url);
			List<Dlc> dlcs = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Dlc>>() {
			}).getBody();
			LOG.debug("Found {} dlcs for a game with id: {}", dlcs.size(), gameId);
			
			return dlcs;
		} catch (Exception ex) {
			LOG.warn("Got an exception while requesting dlcs, return zero dlcs: {}", ex.getMessage());
			return new ArrayList<>();
		}
		
	}

	@Override
	public List<Event> getEvents(int gameId) {
		try {
			String url = eventServiceUrl + gameId;
			LOG.debug("Will call getEvents API on URL: {}", url);
			List<Event> events = restTemplate
					.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Event>>() {
					}).getBody();
			LOG.debug("Found {} events for a game with id: {}", events.size(), gameId);
			
			return events;
		} catch (Exception ex) {
			LOG.warn("Got an exception while requesting events, return zero events: {}", ex.getMessage());
			return new ArrayList<>();
		}
		
	}

	@Override
	public Event createEvent(Event body) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteEvents(int gameId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Dlc createDlc(Dlc body) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteDlcs(int gameId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Review createReview(Review body) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteReviews(int gameId) {
		// TODO Auto-generated method stub
		
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
