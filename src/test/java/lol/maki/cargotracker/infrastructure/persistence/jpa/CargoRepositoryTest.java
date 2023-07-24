package lol.maki.cargotracker.infrastructure.persistence.jpa;

import jakarta.persistence.EntityManager;
import lol.maki.cargotracker.application.util.DateUtils;
import lol.maki.cargotracker.domain.model.cargo.*;
import lol.maki.cargotracker.infrastructure.sampledata.SampleLocations;
import lol.maki.cargotracker.infrastructure.sampledata.SampleVoyages;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import lol.maki.cargotracker.domain.model.handling.HandlingEvent;
import lol.maki.cargotracker.domain.model.handling.HandlingEventRepository;
import lol.maki.cargotracker.domain.model.location.Location;
import lol.maki.cargotracker.domain.model.location.LocationRepository;
import lol.maki.cargotracker.domain.model.location.UnLocode;
import lol.maki.cargotracker.domain.model.voyage.Voyage;
import lol.maki.cargotracker.domain.model.voyage.VoyageNumber;
import lol.maki.cargotracker.domain.model.voyage.VoyageRepository;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static lol.maki.cargotracker.domain.model.handling.HandlingEvent.Type.LOAD;
import static lol.maki.cargotracker.domain.model.handling.HandlingEvent.Type.RECEIVE;

@DataJpaTest
@Import(TestConfig.class)
class CargoRepositoryTest {

	@Autowired
	CargoRepository cargoRepository;

	@Autowired
	LocationRepository locationRepository;

	@Autowired
	VoyageRepository voyageRepository;

	@Autowired
	HandlingEventRepository handlingEventRepository;

	@Autowired
	EntityManager entityManager;

	@Test
	void testFindByCargoId() {
		final TrackingId trackingId = new TrackingId("ABC123");
		final Cargo cargo = cargoRepository.find(trackingId);
		assertThat(cargo).isNotNull();
		assertThat(cargo.origin()).isEqualTo(SampleLocations.HONGKONG);
		assertThat(cargo.routeSpecification().origin()).isEqualTo(SampleLocations.HONGKONG);
		assertThat(cargo.routeSpecification().destination()).isEqualTo(SampleLocations.HELSINKI);

		assertThat(cargo.delivery()).isNotNull();

		final List<HandlingEvent> events = handlingEventRepository.lookupHandlingHistoryOfCargo(trackingId)
			.distinctEventsByCompletionTime();
		assertThat(events).hasSize(3);

		HandlingEvent firstEvent = events.get(0);
		assertHandlingEvent(cargo, firstEvent, RECEIVE, SampleLocations.HONGKONG, DateUtils.toDate("2009-03-01"),
				Instant.now(), Voyage.NONE.voyageNumber());

		HandlingEvent secondEvent = events.get(1);

		assertHandlingEvent(cargo, secondEvent, LOAD, SampleLocations.HONGKONG, DateUtils.toDate("2009-03-02"),
				Instant.now(), new VoyageNumber("0100S"));

		List<Leg> legs = cargo.itinerary().legs();
		assertThat(legs).hasSize(3)
			.extracting("voyage.voyageNumber", "loadLocation", "unloadLocation")
			.containsExactly(Tuple.tuple(null, SampleLocations.HONGKONG, SampleLocations.NEWYORK),
					Tuple.tuple("0200T", SampleLocations.NEWYORK, SampleLocations.DALLAS),
					Tuple.tuple("0300A", SampleLocations.DALLAS, SampleLocations.HELSINKI));
	}

	private void assertHandlingEvent(Cargo cargo, HandlingEvent event, HandlingEvent.Type expectedEventType,
			Location expectedLocation, Instant expectedCompletionTime, Instant expectedRegistrationTime,
			VoyageNumber voyage) {
		assertThat(event.type()).isEqualTo(expectedEventType);
		assertThat(event.location()).isEqualTo(expectedLocation);
		assertThat(event.completionTime()).isEqualTo(expectedCompletionTime);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm").withZone(ZoneOffset.UTC);
		assertThat(formatter.format(event.registrationTime())).isEqualTo(formatter.format(expectedRegistrationTime));

		assertThat(event.voyage().voyageNumber()).isEqualTo(voyage);
		assertThat(event.cargo()).isEqualTo(cargo);
	}

	@Test
	void testFindByCargoIdUnknownId() {
		assertThat(cargoRepository.find(new TrackingId("UNKNOWN"))).isNull();
	}

	@Test
	void testSave() {
		TrackingId trackingId = new TrackingId("AAA");
		Location origin = locationRepository.find(SampleLocations.STOCKHOLM.unLocode());
		Location destination = locationRepository.find(SampleLocations.MELBOURNE.unLocode());

		Cargo cargo = new Cargo(trackingId, new RouteSpecification(origin, destination, Instant.now()));
		cargoRepository.store(cargo);

		Voyage voyage = voyageRepository.find(SampleVoyages.NEW_YORK_TO_DALLAS.voyageNumber());
		assertThat(voyage).isNotNull();
		cargo.assignToRoute(
				new Itinerary(List.of(new Leg(voyage, locationRepository.find(SampleLocations.STOCKHOLM.unLocode()),
						locationRepository.find(SampleLocations.MELBOURNE.unLocode()), Instant.now(), Instant.now()))));

		flush();

		Cargo result = entityManager
			.createQuery("from Cargo c where c.trackingId = '%s'".formatted(trackingId.idString()), Cargo.class)
			.getSingleResult();
		assertThat(result.trackingId().idString()).isEqualTo("AAA");
		assertThat(result.routeSpecification.origin.id).isEqualTo(origin.id);
		assertThat(result.routeSpecification.destination.id).isEqualTo(destination.id);

		entityManager.clear();

		final Cargo loadedCargo = cargoRepository.find(trackingId);
		assertThat(loadedCargo.itinerary().legs()).hasSize(1);
	}

	@Test
	void testReplaceItinerary() {
		Cargo cargo = cargoRepository.find(new TrackingId("JKL567"));
		assertThat(cargo).isNotNull();
		long cargoId = cargo.id;
		assertThat(countLegsForCargo(cargoId)).isEqualTo(3);

		Location legFrom = locationRepository.find(new UnLocode("FIHEL"));
		Location legTo = locationRepository.find(new UnLocode("CNHKG"));
		Voyage voyage = voyageRepository.find(SampleVoyages.HELSINKI_TO_HONGKONG.voyageNumber());
		Itinerary newItinerary = new Itinerary(List.of(new Leg(voyage, legFrom, legTo, Instant.now(), Instant.now())));

		cargo.assignToRoute(newItinerary);

		cargoRepository.store(cargo);
		flush();

		assertThat(countLegsForCargo(cargoId)).isEqualTo(1);
	}

	@Test
	void testFindAll() {
		List<Cargo> all = cargoRepository.getAll();
		assertThat(all).isNotNull();
		assertThat(all).hasSize(2);
	}

	@Test
	void testNextTrackingId() {
		TrackingId trackingId = cargoRepository.nextTrackingId();
		assertThat(trackingId).isNotNull();

		TrackingId trackingId2 = cargoRepository.nextTrackingId();
		assertThat(trackingId2).isNotNull();
		assertThat(trackingId.equals(trackingId2)).isFalse();
	}

	private void flush() {
		entityManager.flush();
	}

	private int countLegsForCargo(long cargoId) {
		return ((Long) entityManager
			.createNativeQuery("select count(*) from Leg l where l.cargo_id = %d".formatted(cargoId))
			.getSingleResult()).intValue();
	}

}