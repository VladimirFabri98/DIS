package vladimir.microservices.core.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import vladimir.microservices.core.event.persistence.EventEntity;
import vladimir.microservices.core.event.persistence.EventRepository;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class PersistenceTests {

	@Autowired
	private EventRepository repository;

	private EventEntity savedEntity;
	private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	private long date;

	@BeforeEach
	public void setupDb() {
		repository.deleteAll();
		try {
			date = sdf.parse("04-08-2023").getTime();
		} catch (ParseException e) {}
		EventEntity entity = new EventEntity(1, 1, "t", "n", new Date(date));
		savedEntity = repository.save(entity);

		assertEqualsEvent(entity, savedEntity);
	}

	@Test
	public void create() {

		EventEntity newEntity = new EventEntity(1, 2, "t", "n", new Date(date));
		repository.save(newEntity);

		EventEntity foundEntity = repository.findById(newEntity.getId()).get();
		assertEqualsEvent(newEntity, foundEntity);

		Assertions.assertEquals(2, repository.count());
	}

	@Test
	public void update() {
		savedEntity.setName("n1");
		repository.save(savedEntity);

		EventEntity foundEntity = repository.findById(savedEntity.getId()).get();
		Assertions.assertEquals(1, (long) foundEntity.getVersion());
		Assertions.assertEquals("n1", foundEntity.getName());
	}

	@Test
	public void delete() {
		repository.delete(savedEntity);
		Assertions.assertFalse(repository.existsById(savedEntity.getId()));
	}

	@Test
	public void getByGameId() {
		List<EventEntity> entityList = repository.findByGameId(savedEntity.getGameId());

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
		EventEntity entity1 = repository.findById(savedEntity.getId()).get();
		EventEntity entity2 = repository.findById(savedEntity.getId()).get();

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
		EventEntity updatedEntity = repository.findById(savedEntity.getId()).get();
		Assertions.assertEquals(1, (int) updatedEntity.getVersion());
		Assertions.assertEquals("n1", updatedEntity.getName());
	}

	private void assertEqualsEvent(EventEntity expectedEntity, EventEntity actualEntity) {
		Assertions.assertEquals(expectedEntity.getId(), actualEntity.getId());
		Assertions.assertEquals(expectedEntity.getVersion(), actualEntity.getVersion());
		Assertions.assertEquals(expectedEntity.getGameId(), actualEntity.getGameId());
		Assertions.assertEquals(expectedEntity.getEventId(), actualEntity.getEventId());
		Assertions.assertEquals(expectedEntity.getType(), actualEntity.getType());
		Assertions.assertEquals(expectedEntity.getName(), actualEntity.getName());
		Assertions.assertEquals(expectedEntity.getDateOfStart(), actualEntity.getDateOfStart());
	}
}