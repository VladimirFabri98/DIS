package vladimir.api.core.review;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface ReviewService {

	@GetMapping("review/{gameId}")
	List<Review> getReviews(@PathVariable int gameId);
	
	@PostMapping(value = "/review-post" , consumes = "application/json")
	Review createReview(@RequestBody Review body);
	
	@DeleteMapping("review/{gameId}")
	void deleteReviews(@PathVariable int gameId);
}
