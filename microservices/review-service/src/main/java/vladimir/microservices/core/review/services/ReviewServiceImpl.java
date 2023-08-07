package vladimir.microservices.core.review.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import vladimir.api.core.review.Review;
import vladimir.api.core.review.ReviewService;
import vladimir.util.exceptions.InvalidInputException;
import vladimir.util.exceptions.NotFoundException;
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
	public List<Review> getReviews(int gameId) {
		LOG.debug("/Review return the found review for gameId={}", gameId);
        if (gameId < 1) throw new InvalidInputException("Invalid gameId: " + gameId);
        
        if (gameId == 200) {
            LOG.debug("No reviews found for gameId: {}", gameId);
            return  new ArrayList<>();
        }
        
        List<Review> list = new ArrayList<>();
        list.add(new Review(1, gameId, 4, serviceUtil.getServiceAddress()));
        list.add(new Review(2, gameId, 5, serviceUtil.getServiceAddress()));
        list.add(new Review(3, gameId, 3, serviceUtil.getServiceAddress()));
        
        LOG.debug("/review response size: {}", list.size());
        
        return list;
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
}
