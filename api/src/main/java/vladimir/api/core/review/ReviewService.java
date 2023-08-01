package vladimir.api.core.review;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface ReviewService {

	@GetMapping("review/{reviewId}")
	Review getReview(@PathVariable int reviewId);
}
