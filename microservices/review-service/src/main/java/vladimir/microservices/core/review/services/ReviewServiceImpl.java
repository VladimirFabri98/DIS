package vladimir.microservices.core.review.services;

import static reactor.core.publisher.Mono.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vladimir.api.core.review.Review;
import vladimir.api.core.review.ReviewService;
import vladimir.microservices.core.review.persistence.ReviewEntity;
import vladimir.microservices.core.review.persistence.ReviewRepository;
import vladimir.util.exceptions.InvalidInputException;
import vladimir.util.exceptions.NotFoundException;
import vladimir.util.http.ServiceUtil;

@RestController
public class ReviewServiceImpl implements ReviewService{

	private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);
	private final ServiceUtil serviceUtil;
	private final ReviewMapperImpl mapper;
	private final ReviewRepository repository;
	
	@Autowired
	public ReviewServiceImpl(ServiceUtil serviceUtil, ReviewMapperImpl mapper, ReviewRepository repository) {
		this.serviceUtil = serviceUtil;
		this.mapper = mapper;
		this.repository = repository;
	}
	

	@Override
	public Flux<Review> getReviews(int gameId) {
		if (gameId < 1) throw new InvalidInputException("Invalid gameId: " + gameId);

        return repository.findByGameId(gameId)
            .switchIfEmpty(error(new NotFoundException("No reviews found for gameId: " + gameId)))
            .log()
            .map(e -> mapper.entityToApi(e))
            .map(e -> {e.setServiceAddress(serviceUtil.getServiceAddress()); return e;});
	}

	@Override
	public Review createReview(Review body) {
		if (body.getGameId() < 1) throw new InvalidInputException("Invalid gameId: " + body.getGameId());

        ReviewEntity entity = mapper.apiToEntity(body);
        Mono<Review> newEntity = repository.save(entity)
            .log()
            .onErrorMap(
                DuplicateKeyException.class,
                ex ->  new InvalidInputException("Duplicate key, Game Id: " + body.getGameId() + ", Review Id:" + body.getReviewId()))
            .map(e -> mapper.entityToApi(e));

        return newEntity.block();
	}

	@Override
	public void deleteReviews(int gameId) {
		if(gameId < 1) throw new InvalidInputException("Invalid gameId: " + gameId);
		LOG.debug("deleteReview: tries to delete an entity with gameId: {}", gameId);
		repository.deleteAll(repository.findByGameId(gameId)).block();

	}
}
