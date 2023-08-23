package vladimir.microservices.core.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.OptimisticLockingFailureException;

import vladimir.microservices.core.event.persistence.GameEventEntity;
import vladimir.microservices.core.event.persistence.GameEventRepository;


@DataMongoTest(properties = {"spring.cloud.config.enabled=false"})
public class PersistenceTests {

	@Autowired
	private GameEventRepository repository;

	private GameEventEntity savedEntity;
	private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	private long date;

	@BeforeEach
	public void setupDb() {
		repository.deleteAll();
		try {
			date = sdf.parse("04-08-2023").getTime();
		} catch (ParseException e) {}
		GameEventEntity entity = new GameEventEntity(1, 1, "t", "n", new Date(date));
		savedEntity = repository.save(entity).block();

		assertEqualsEvent(entity, savedEntity);
	}

	@Test
	public void create() {

		GameEventEntity newEntity = new GameEventEntity(1, 2, "t", "n", new Date(date));
		repository.save(newEntity);

		GameEventEntity foundEntity = repository.findById(newEntity.getId()).block();
		assertEqualsEvent(newEntity, foundEntity);

		Assertions.assertEquals(2, repository.count());
	}

	@Test
	public void update() {
		savedEntity.setName("n1");
		repository.save(savedEntity);

		GameEventEntity foundEntity = repository.findById(savedEntity.getId()).block();
		Assertions.assertEquals(1, (long) foundEntity.getVersion());
		Assertions.assertEquals("n1", foundEntity.getName());
	}

	@Test
	public void delete() {
		repository.delete(savedEntity);
		Assertions.assertFalse(repository.existsById(savedEntity.getId()).block());
	}

	@Test
	public void getByGameId() {
		List<GameEventEntity> entityList = repository.findByGameId(savedEntity.getGameId()).collectList().block();

		assertThat(entityList.size() == 1);
		assertEqualsEvent(savedEntity, entityList.get(0));
	}

	/*@Test()
	public void duplicateError() {

		EventEntity entity = new EventEntity(1, 2, "t", "n", new Date(date));
		Assertions.assertThrows(DuplicateKeyException.class, () -> repository.save(entity));

	}*/

	@Test
	public void optimisticLockError() {

		// Store the saved entity in two separate entity objects
		GameEventEntity entity1 = repository.findById(savedEntity.getId()).block();
		GameEventEntity entity2 = repository.findById(savedEntity.getId()).block();

		// Update the entity using the first entity object
		entity1.setName("n1");
		repository.save(entity1);

		// Update the entity using the second entity object.
		// This should fail since the second entity now holds a old version number, i.e.
		// a Optimistic Lock Error
		try {
			entity2.setName("n1");
			repository.save(entity2);

			fail("Expected an OptimisticLockingFailureException");
		} catch (OptimisticLockingFailureException e) {
		}

		// Get the updated entity from the database and verify its new sate
		GameEventEntity updatedEntity = repository.findById(savedEntity.getId()).block();
		Assertions.assertEquals(1, (int) updatedEntity.getVersion());
		Assertions.assertEquals("n1", updatedEntity.getName());
	}

	private void assertEqualsEvent(GameEventEntity expectedEntity, GameEventEntity actualEntity) {
		Assertions.assertEquals(expectedEntity.getId(), actualEntity.getId());
		Assertions.assertEquals(expectedEntity.getVersion(), actualEntity.getVersion());
		Assertions.assertEquals(expectedEntity.getGameId(), actualEntity.getGameId());
		Assertions.assertEquals(expectedEntity.getEventId(), actualEntity.getEventId());
		Assertions.assertEquals(expectedEntity.getType(), actualEntity.getType());
		Assertions.assertEquals(expectedEntity.getName(), actualEntity.getName());
		Assertions.assertEquals(expectedEntity.getDateOfStart(), actualEntity.getDateOfStart());
	}
}