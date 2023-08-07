package vladimir.microservices.core.review.services;

import java.util.ArrayList;
import java.util.List;
import vladimir.api.core.review.Review;
import vladimir.microservices.core.review.persistence.ReviewEntity;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapperImpl implements ReviewMapper
{
    public Review entityToApi(final ReviewEntity entity) {
        if (entity == null) {
            return null;
        }
        final Review review = new Review();
        review.setReviewId(entity.getReviewId());
        review.setGameId(entity.getGameId());
        review.setRating(entity.getRating());
        return review;
    }
    
    public ReviewEntity apiToEntity(final Review api) {
        if (api == null) {
            return null;
        }
        final ReviewEntity reviewEntity = new ReviewEntity();
        reviewEntity.setGameId(api.getGameId());
        reviewEntity.setReviewId(api.getReviewId());
        reviewEntity.setRating(api.getRating());
        return reviewEntity;
    }
    
    public List<Review> entityListToApiList(final List<ReviewEntity> entity) {
        if (entity == null) {
            return null;
        }
        final List<Review> list = new ArrayList<Review>(entity.size());
        for (final ReviewEntity reviewEntity : entity) {
            list.add(this.entityToApi(reviewEntity));
        }
        return list;
    }
    
    public List<ReviewEntity> apiListToEntityList(final List<Review> api) {
        if (api == null) {
            return null;
        }
        final List<ReviewEntity> list = new ArrayList<ReviewEntity>(api.size());
        for (final Review review : api) {
            list.add(this.apiToEntity(review));
        }
        return list;
    }
}
