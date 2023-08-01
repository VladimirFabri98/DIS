package vladimir.api.composite.game;

public class ReviewSummary {

	private int reviewId;
	private double rating;

	public ReviewSummary(int reviewId, double rating) {
		super();
		this.reviewId = reviewId;
		this.rating = rating;
	}

	public int getReviewId() {
		return reviewId;
	}

	public void setReviewId(int reviewId) {
		this.reviewId = reviewId;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

}
