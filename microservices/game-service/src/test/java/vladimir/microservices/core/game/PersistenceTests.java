package vladimir.microservices.core.game;

import static org.assertj.core.api.Assertions.fail;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;

import vladimir.microservices.core.game.persistence.GameEntity;
import vladimir.microservices.core.game.persistence.GameRepository;


@DataMongoTest
public class PersistenceTests {

	@Autowired
    private GameRepository repository;

    private GameEntity savedEntity;

    @BeforeEach
   	public void setupDb() {
   		repository.deleteAll();

        GameEntity entity = new GameEntity(1, "n", "p",2000);
        savedEntity = repository.save(entity);

        assertEqualsGame(entity, savedEntity);
    }


    @Test
   	public void create() {

        GameEntity newEntity = new GameEntity(2, "n", "p",2000);
        repository.save(newEntity);

        GameEntity foundEntity = repository.findById(newEntity.getId()).get();
        assertEqualsGame(newEntity, foundEntity);

        Assertions.assertEquals(2, repository.count());
    }

    @Test
   	public void update() {
        savedEntity.setName("n2");
        repository.save(savedEntity);

        GameEntity foundEntity = repository.findById(savedEntity.getId()).get();
        Assertions.assertEquals(1, (long)foundEntity.getVersion());
        Assertions.assertEquals("n2", foundEntity.getName());
    }

    @Test
   	public void delete() {
        repository.delete(savedEntity);
        Assertions.assertFalse(repository.existsById(savedEntity.getId()));
    }

    @Test
   	public void getByGameId() {
        Optional<GameEntity> entity = repository.findByGameId(savedEntity.getGameId());

        Assertions.assertTrue(entity.isPresent());
        assertEqualsGame(savedEntity, entity.get());
    }

    @Test()
   	public void duplicateError() {
    		GameEntity entity = new GameEntity(savedEntity.getGameId(), "n","p",2000);
            Assertions.assertThrows(DuplicateKeyException.class, () -> repository.save(entity));
    }

    @Test
   	public void optimisticLockError() {

        // Store the saved entity in two separate entity objects
        GameEntity entity1 = repository.findById(savedEntity.getId()).get();
        GameEntity entity2 = repository.findById(savedEntity.getId()).get();

        // Update the entity using the first entity object
        entity1.setName("n1");
        repository.save(entity1);

        //  Update the entity using the second entity object.
        // This should fail since the second entity now holds a old version number, i.e. a Optimistic Lock Error
        try {
            entity2.setName("n2");
            repository.save(entity2);

            fail("Expected an OptimisticLockingFailureException");
        } catch (OptimisticLockingFailureException e) {}

        // Get the updated entity from the database and verify its new sate
        GameEntity updatedEntity = repository.findById(savedEntity.getId()).get();
        Assertions.assertEquals(1, (int)updatedEntity.getVersion());
        Assertions.assertEquals("n1", updatedEntity.getName());
    }
    
    private void assertEqualsGame(GameEntity expectedEntity, GameEntity actualEntity) {
    	Assertions.assertEquals(expectedEntity.getId(), actualEntity.getId());
        Assertions.assertEquals(expectedEntity.getVersion(), actualEntity.getVersion());
        Assertions.assertEquals(expectedEntity.getGameId(),actualEntity.getGameId());
        Assertions.assertEquals(expectedEntity.getName(),actualEntity.getName());
        Assertions.assertEquals(expectedEntity.getProducer(),actualEntity.getProducer());
        Assertions.assertEquals(expectedEntity.getReleaseYear(),actualEntity.getReleaseYear());
    }
	
}
