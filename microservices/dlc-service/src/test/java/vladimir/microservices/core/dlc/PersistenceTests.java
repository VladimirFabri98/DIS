package vladimir.microservices.core.dlc;

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

import vladimir.microservices.core.dlc.persistence.DlcEntity;
import vladimir.microservices.core.dlc.persistence.DlcRepository;

@DataMongoTest
public class PersistenceTests {

	@Autowired
    private DlcRepository repository;

    private DlcEntity savedEntity;

    @BeforeEach
   	public void setupDb() {
   		repository.deleteAll();

        DlcEntity entity = new DlcEntity(1,1,"n",200);
        savedEntity = repository.save(entity);

        assertEqualsDlc(entity, savedEntity);
    }


    @Test
   	public void create() {

        DlcEntity newEntity = new DlcEntity(1,2,"n",200);
        repository.save(newEntity);

        DlcEntity foundEntity = repository.findById(newEntity.getId()).get();
        assertEqualsDlc(newEntity, foundEntity);

        Assertions.assertEquals(2, repository.count());
    }

    @Test
   	public void update() {
        savedEntity.setName("n2");
        repository.save(savedEntity);

        DlcEntity foundEntity = repository.findById(savedEntity.getId()).get();
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
    	List<DlcEntity> entityList = repository.findByGameId(savedEntity.getGameId());

        assertThat(entityList.size() == 1);
        assertEqualsDlc(savedEntity, entityList.get(0));
    }

    @Test()
   	public void duplicateError() {
    	try {
    		DlcEntity entity = new DlcEntity(1,1,"n",200);
            repository.save(entity);
            
            Assertions.fail("Expected DuplicateKeyException");
		} catch (DuplicateKeyException e) {}
        
    }

    @Test
   	public void optimisticLockError() {

        // Store the saved entity in two separate entity objects
        DlcEntity entity1 = repository.findById(savedEntity.getId()).get();
        DlcEntity entity2 = repository.findById(savedEntity.getId()).get();

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
        DlcEntity updatedEntity = repository.findById(savedEntity.getId()).get();
        Assertions.assertEquals(1, (int)updatedEntity.getVersion());
        Assertions.assertEquals("n1", updatedEntity.getName());
    }
    
    private void assertEqualsDlc(DlcEntity expectedEntity, DlcEntity actualEntity) {
    	Assertions.assertEquals(expectedEntity.getId(), actualEntity.getId());
        Assertions.assertEquals(expectedEntity.getVersion(), actualEntity.getVersion());
        Assertions.assertEquals(expectedEntity.getGameId(),actualEntity.getGameId());
        Assertions.assertEquals(expectedEntity.getDlcId(),actualEntity.getDlcId());
        Assertions.assertEquals(expectedEntity.getName(),actualEntity.getName());
        Assertions.assertEquals(expectedEntity.getPrice(),actualEntity.getPrice());
    }
}
