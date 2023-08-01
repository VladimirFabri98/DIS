package vladimir.api.core.review;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface ReviewService {

	@GetMapping("review/{gameId}")
	List<Review> getReviews(@PathVariable int gameId);
}
