package vladimir.microservices.core.review.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;

import vladimir.api.core.review.Review;
import vladimir.api.core.review.ReviewService;
import vladimir.microservices.core.review.persistence.ReviewEntity;
import vladimir.microservices.core.review.persistence.ReviewRepository;
import vladimir.util.exceptions.InvalidInputException;
import vladimir.util.http.ServiceUtil;

@RestController
public class ReviewServiceImpl implements ReviewService{

	private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);
	private final ServiceUtil serviceUtil;
	private final ReviewMapper mapper;
	private final ReviewRepository repository;
	
	@Autowired
	public ReviewServiceImpl(ServiceUtil serviceUtil, ReviewMapper mapper, ReviewRepository repository) {
		this.serviceUtil = serviceUtil;
		this.mapper = mapper;
		this.repository = repository;
	}
	

	@Override
	public List<Review> getReviews(int gameId) {
        if (gameId < 1) throw new InvalidInputException("Invalid gameId: " + gameId);
        
        List<ReviewEntity> listEntity = repository.findByGameId(gameId);
        List<Review> listApi = mapper.entityListToApiList(listEntity);
        listApi.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));
        
        
        LOG.debug("/review response size: {}", listApi.size());
        
        return listApi;
	}

	@Override
	public Review createReview(Review body) {
		try {
			ReviewEntity entity = mapper.apiToEntity(body);
			ReviewEntity newEntity = repository.save(entity);
			Review response =  mapper.entityToApi(newEntity);
			
			LOG.debug("createReview: created a review entity: {}/{}", body.getGameId(),body.getReviewId());
			
			return response;
		} catch (DuplicateKeyException e) {
			throw new InvalidInputException("Duplicate key, gameId: "+ body.getGameId() + ", reviewId: " + body.getReviewId());
		}
		
	}

	@Override
	public void deleteReviews(int gameId) {
		LOG.debug("deleteReviews: tries to delete reviews for the game with gameId: {}", gameId);
		repository.deleteAll(repository.findByGameId(gameId));
		
	}
}
