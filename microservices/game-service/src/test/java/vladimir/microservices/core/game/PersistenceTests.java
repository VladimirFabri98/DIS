package vladimir.microservices.core.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;

import reactor.test.StepVerifier;
import vladimir.microservices.core.game.persistence.GameEntity;
import vladimir.microservices.core.game.persistence.GameRepository;


@DataMongoTest
public class PersistenceTests {

	@Autowired
    private GameRepository repository;

    private GameEntity savedEntity;

    @BeforeEach
   	public void setupDb() {
   		StepVerifier.create(repository.deleteAll()).verifyComplete();

        GameEntity entity = new GameEntity(1, "n", "p",2000);
       StepVerifier.create(repository.save(entity)).expectNextMatches(createdEntity -> {
       		savedEntity = createdEntity;
       		return areGameEqual(entity,savedEntity);
       })
       .verifyComplete();
    }


    @Test
   	public void create() {

        GameEntity newEntity = new GameEntity(2, "n", "p",2000);
        
        StepVerifier.create(repository.save(newEntity)).expectNextMatches(
        		createdEntity -> newEntity.getGameId() == createdEntity.getGameId()).verifyComplete();
        
        StepVerifier.create(repository.findByGameId(newEntity.getGameId()))
        .expectNextMatches(foundEntity -> areGameEqual(foundEntity, newEntity)).verifyComplete();

        StepVerifier.create(repository.count()).expectNext((long) 2).verifyComplete();
    }

    @Test
   	public void update() {
        savedEntity.setName("n2");
       
        StepVerifier.create(repository.save(savedEntity))
        .expectNextMatches(updatedEntity -> updatedEntity.getName().equals("n2")).verifyComplete();
        
        StepVerifier.create(repository.findByGameId(savedEntity.getGameId()))
        .expectNextMatches(foundEntity -> foundEntity.getVersion() == 1 && foundEntity.getName().equals("n2")).verifyComplete();

    }

    @Test
   	public void delete() {
        StepVerifier.create(repository.delete(savedEntity)).verifyComplete();
        StepVerifier.create(repository.existsById(savedEntity.getId())).expectNext(false).verifyComplete();
    }

    @Test
   	public void getByGameId() {
    	StepVerifier.create(repository.findByGameId(savedEntity.getGameId())).
    	expectNextMatches(foundEntity -> areGameEqual(foundEntity,savedEntity)).verifyComplete();    	
    }

    @Test()
   	public void duplicateError() {
    		GameEntity entity = new GameEntity(savedEntity.getGameId(), "n","p",2000);
    		StepVerifier.create(repository.save(entity)).expectError(DuplicateKeyException.class).verify();
    }

    @Test
   	public void optimisticLockError() {

        // Store the saved entity in two separate entity objects
        GameEntity entity1 = repository.findById(savedEntity.getId()).block();
        GameEntity entity2 = repository.findById(savedEntity.getId()).block();

        // Update the entity using the first entity object
        entity1.setName("n1");
        repository.save(entity1).block();

        //  Update the entity using the second entity object.
        // This should fail since the second entity now holds a old version number, i.e. a Optimistic Lock Error
        entity2.setName("n2");
        StepVerifier.create(repository.save(entity2)).expectError(OptimisticLockingFailureException.class).verify();
          
        // Get the updated entity from the database and verify its new sate
        StepVerifier.create(repository.findById(savedEntity.getId())).
        expectNextMatches(updatedEntity -> updatedEntity.getVersion() == 1 && updatedEntity.getName().equals("n1")).verifyComplete();
    }
    
    private boolean areGameEqual(GameEntity expectedEntity, GameEntity actualEntity) {
    	return
                (expectedEntity.getId().equals(actualEntity.getId())) &&
                (expectedEntity.getVersion() == actualEntity.getVersion()) &&
                (expectedEntity.getGameId() == actualEntity.getGameId()) &&
                (expectedEntity.getName().equals(actualEntity.getName())) &&
                (expectedEntity.getProducer() == actualEntity.getProducer());
    }
	
}
