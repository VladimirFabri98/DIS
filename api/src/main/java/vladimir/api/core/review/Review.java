package vladimir.api.core.review;

public class Review {

	private int reviewId;
	private int gameId;
	private double rating;
	private final String serviceAdress;

	public Review(int reviewId, int gameId, double rating, String serviceAdress) {
		super();
		this.reviewId = reviewId;
		this.gameId = gameId;
		this.rating = rating;
		this.serviceAdress = serviceAdress;
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

	public String getServiceAdress() {
		return serviceAdress;
	}

}
