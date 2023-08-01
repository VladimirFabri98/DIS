package vladimir.api.core.review;

public class Review {

	private int reviewId;
	private int gameId;
	private double rating;
	private final String serviceAddress;

	public Review() {
		this.reviewId = 0;
		this.gameId = 0;
		this.rating = 0;
		this.serviceAddress = null;
	}
	
	public Review(int reviewId, int gameId, double rating, String serviceAddress) {
		super();
		this.reviewId = reviewId;
		this.gameId = gameId;
		this.rating = rating;
		this.serviceAddress = serviceAddress;
	}

	public int getReviewId() {
		return reviewId;
	}

	public void setReviewId(int reviewId) {
		this.reviewId = reviewId;
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public String getServiceAddress() {
		return serviceAddress;
	}

}
