package lol.maki.cargotracker.infrastructure.persistence.jpa;

import jakarta.persistence.EntityManager;
import lol.maki.cargotracker.domain.model.cargo.Cargo;
import lol.maki.cargotracker.domain.model.cargo.CargoRepository;
import lol.maki.cargotracker.domain.model.cargo.TrackingId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import lol.maki.cargotracker.domain.model.handling.HandlingEvent;
import lol.maki.cargotracker.domain.model.handling.HandlingEventRepository;
import lol.maki.cargotracker.domain.model.location.Location;
import lol.maki.cargotracker.domain.model.location.LocationRepository;
import lol.maki.cargotracker.domain.model.location.UnLocode;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestConfig.class)
class HandlingEventRepositoryTest {

	@Autowired
	HandlingEventRepository handlingEventRepository;

	@Autowired
	CargoRepository cargoRepository;

	@Autowired
	LocationRepository locationRepository;

	@Autowired
	EntityManager entityManager;

	@Test
	void testSave() {
		Location location = locationRepository.find(new UnLocode("SESTO"));

		Cargo cargo = cargoRepository.find(new TrackingId("ABC123"));
		Instant completionTime = Instant.ofEpochMilli(10);
		Instant registrationTime = Instant.ofEpochMilli(20);
		HandlingEvent event = new HandlingEvent(cargo, completionTime, registrationTime, HandlingEvent.Type.CLAIM,
				location);

		handlingEventRepository.store(event);

		flush();

		HandlingEvent result = entityManager
			.createQuery("select he from HandlingEvent he where he.id = %d".formatted(event.id), HandlingEvent.class)
			.getSingleResult();

		assertThat(result.cargo.id).isEqualTo(cargo.id);
		Instant completionDate = result.completionTime;
		assertThat(completionDate).isEqualTo(Instant.ofEpochMilli(10));
		Instant registrationDate = result.registrationTime;
		assertThat(registrationDate).isEqualTo(Instant.ofEpochMilli(20));
		assertThat(result.type).isEqualTo(HandlingEvent.Type.CLAIM);
		// TODO: the rest of the columns
	}

	private void flush() {
		entityManager.flush();
	}

	@Test
	void testFindEventsForCargo() {
		TrackingId trackingId = new TrackingId("ABC123");
		List<HandlingEvent> handlingEvents = handlingEventRepository.lookupHandlingHistoryOfCargo(trackingId)
			.distinctEventsByCompletionTime();
		assertThat(handlingEvents).hasSize(3);
	}

}