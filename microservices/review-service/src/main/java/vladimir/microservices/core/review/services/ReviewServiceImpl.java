package vladimir.microservices.core.review.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import vladimir.api.core.review.Review;
import vladimir.api.core.review.ReviewService;
import vladimir.util.exceptions.InvalidInputException;
import vladimir.util.http.ServiceUtil;

@RestController
public class ReviewServiceImpl implements ReviewService{

	private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);
	private final ServiceUtil serviceUtil;
	
	@Autowired
	public ReviewServiceImpl(ServiceUtil serviceUtil) {
		this.serviceUtil = serviceUtil;
	}
	
	@Override
	public Review getReview(int reviewId) {
		LOG.debug("/Review return the found review for reviewId={}", reviewId);

        if (reviewId < 1) throw new InvalidInputException("Invalid reviewId: " + reviewId);
		
		return new Review(reviewId, reviewId, 4, serviceUtil.getServiceAddress());
	}
}
