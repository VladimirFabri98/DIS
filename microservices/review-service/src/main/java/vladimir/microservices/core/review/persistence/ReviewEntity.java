package vladimir.microservices.core.review.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "reviews")
@CompoundIndex(name = "game-review-id", unique = true, def = "{'gameId':1, 'reviewId':1}")
public class ReviewEntity {

	@Id
	private String id;

	@Version
	private Integer version;

	private int gameId;
	private int reviewId;
	private double rating;

	public ReviewEntity() {
		super();
	}

	public ReviewEntity(int gameId, int reviewId, double rating) {
		super();
		this.gameId = gameId;
		this.reviewId = reviewId;
		this.rating = rating;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
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
	
	public Integer getVersion() {
		return version;
	}

}
