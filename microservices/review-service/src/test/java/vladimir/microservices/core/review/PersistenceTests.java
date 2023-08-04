package vladimir.microservices.core.review;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;

import vladimir.microservices.core.review.persistence.ReviewEntity;
import vladimir.microservices.core.review.persistence.ReviewRepository;

@DataMongoTest
public class PersistenceTests {

	@Autowired
    private ReviewRepository repository;

    private ReviewEntity savedEntity;

    @BeforeEach
   	public void setupDb() {
   		repository.deleteAll();

        ReviewEntity entity = new ReviewEntity(1,1,3.5);
        savedEntity = repository.save(entity);

        assertEqualsReview(entity, savedEntity);
    }


    @Test
   	public void create() {

        ReviewEntity newEntity = new ReviewEntity(1,2,3.5);
        repository.save(newEntity);

        ReviewEntity foundEntity = repository.findById(newEntity.getId()).get();
        assertEqualsReview(newEntity, foundEntity);

        Assertions.assertEquals(2, repository.count());
    }

    @Test
   	public void update() {
        savedEntity.setRating(4);
        repository.save(savedEntity);

        ReviewEntity foundEntity = repository.findById(savedEntity.getId()).get();
        Assertions.assertEquals(1, (long)foundEntity.getVersion());
        Assertions.assertEquals(4, foundEntity.getRating());
    }

    @Test
   	public void delete() {
        repository.delete(savedEntity);
        Assertions.assertFalse(repository.existsById(savedEntity.getId()));
    }

    @Test
   	public void getByGameId() {
    	List<ReviewEntity> entityList = repository.findByGameId(savedEntity.getGameId());

        assertThat(entityList.size() == 1);
        assertEqualsReview(savedEntity, entityList.get(0));
    }

    @Test()
   	public void duplicateError() {
    	try {
    		ReviewEntity entity = new ReviewEntity(1,1,3.5);
            repository.save(entity);
            
            Assertions.fail("Expected DuplicateKeyException");
		} catch (DuplicateKeyException e) {}
        
    }

    @Test
   	public void optimisticLockError() {

        // Store the saved entity in two separate entity objects
        ReviewEntity entity1 = repository.findById(savedEntity.getId()).get();
        ReviewEntity entity2 = repository.findById(savedEntity.getId()).get();

        // Update the entity using the first entity object
        entity1.setRating(4);
        repository.save(entity1);

        //  Update the entity using the second entity object.
        // This should fail since the second entity now holds a old version number, i.e. a Optimistic Lock Error
        try {
            entity2.setRating(4);
            repository.save(entity2);

            fail("Expected an OptimisticLockingFailureException");
        } catch (OptimisticLockingFailureException e) {}

        // Get the updated entity from the database and verify its new sate
        ReviewEntity updatedEntity = repository.findById(savedEntity.getId()).get();
        Assertions.assertEquals(1, (int)updatedEntity.getVersion());
        Assertions.assertEquals(4, updatedEntity.getRating());
    }
    
    private void assertEqualsReview(ReviewEntity expectedEntity, ReviewEntity actualEntity) {
    	Assertions.assertEquals(expectedEntity.getId(), actualEntity.getId());
        Assertions.assertEquals(expectedEntity.getVersion(), actualEntity.getVersion());
        Assertions.assertEquals(expectedEntity.getGameId(),actualEntity.getGameId());
        Assertions.assertEquals(expectedEntity.getReviewId(),actualEntity.getReviewId());
        Assertions.assertEquals(expectedEntity.getRating(),actualEntity.getRating());
    }
}