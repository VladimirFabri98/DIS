package vladimir.microservices.composite.game.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
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
		String url = gameServiceUrl + gameId;
		Game game = restTemplate.getForObject(url, Game.class);
		return game;
	}

	@Override
	public List<Review> getReviews(int gameId) {
		String url = reviewServiceUrl + gameId;
		List<Review> reviews = restTemplate
				.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Review>>() {
				}).getBody();
		return reviews;
	}

	@Override
	public List<Dlc> getDlcs(int gameId) {
		String url = dlcServiceUrl + gameId;
		List<Dlc> dlcs = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Dlc>>() {
		}).getBody();
		return dlcs;
	}

	@Override
	public List<Event> getEvents(int gameId) {
		String url = eventServiceUrl + gameId;
		List<Event> events = restTemplate
				.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Event>>() {
				}).getBody();
		return events;
	}

}
