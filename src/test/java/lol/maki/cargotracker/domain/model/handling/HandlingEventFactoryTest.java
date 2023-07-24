package lol.maki.cargotracker.domain.model.handling;

import lol.maki.cargotracker.domain.model.cargo.CargoRepository;
import lol.maki.cargotracker.domain.model.cargo.RouteSpecification;
import lol.maki.cargotracker.domain.model.cargo.TrackingId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import lol.maki.cargotracker.domain.model.cargo.Cargo;
import lol.maki.cargotracker.domain.model.location.LocationRepository;
import lol.maki.cargotracker.domain.model.location.UnLocode;
import lol.maki.cargotracker.domain.model.voyage.Voyage;
import lol.maki.cargotracker.domain.model.voyage.VoyageNumber;
import lol.maki.cargotracker.domain.model.voyage.VoyageRepository;
import lol.maki.cargotracker.infrastructure.persistence.inmemory.LocationRepositoryInMem;
import lol.maki.cargotracker.infrastructure.persistence.inmemory.VoyageRepositoryInMem;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static lol.maki.cargotracker.domain.model.handling.HandlingEvent.Type;
import static lol.maki.cargotracker.infrastructure.sampledata.SampleLocations.*;
import static lol.maki.cargotracker.infrastructure.sampledata.SampleVoyages.CM001;

class HandlingEventFactoryTest {

	private HandlingEventFactory factory;

	private CargoRepository cargoRepository;

	private VoyageRepository voyageRepository;

	private LocationRepository locationRepository;

	private TrackingId trackingId;

	private Cargo cargo;

	@BeforeEach
	void setUp() {

		cargoRepository = mock(CargoRepository.class);
		voyageRepository = new VoyageRepositoryInMem();
		locationRepository = new LocationRepositoryInMem();
		factory = new HandlingEventFactory(cargoRepository, voyageRepository, locationRepository);

		trackingId = new TrackingId("ABC");
		RouteSpecification routeSpecification = new RouteSpecification(TOKYO, HELSINKI, Instant.now());
		cargo = new Cargo(trackingId, routeSpecification);
	}

	@Test
    void testCreateHandlingEventWithCarrierMovement() throws Exception {
        when(cargoRepository.find(trackingId)).thenReturn(cargo);

        VoyageNumber voyageNumber = CM001.voyageNumber();
        UnLocode unLocode = STOCKHOLM.unLocode();
        HandlingEvent handlingEvent = factory.createHandlingEvent(
                Instant.now(), Instant.ofEpochMilli(100), trackingId, voyageNumber, unLocode, Type.LOAD
        );

        assertThat(handlingEvent).isNotNull();
        assertThat(handlingEvent.location()).isEqualTo(STOCKHOLM);
        assertThat(handlingEvent.voyage()).isEqualTo(CM001);
        assertThat(handlingEvent.cargo()).isEqualTo(cargo);
        assertThat(handlingEvent.completionTime()).isEqualTo(Instant.ofEpochMilli(100));
        assertThat(handlingEvent.registrationTime().isBefore(Instant.ofEpochMilli(System.currentTimeMillis() + 1))).isTrue();
    }

	@Test
    void testCreateHandlingEventWithoutCarrierMovement() throws Exception {
        when(cargoRepository.find(trackingId)).thenReturn(cargo);

        UnLocode unLocode = STOCKHOLM.unLocode();
        HandlingEvent handlingEvent = factory.createHandlingEvent(
                Instant.now(), Instant.ofEpochMilli(100), trackingId, null, unLocode, Type.CLAIM
        );

        assertThat(handlingEvent).isNotNull();
        assertThat(handlingEvent.location()).isEqualTo(STOCKHOLM);
        assertThat(handlingEvent.voyage()).isEqualTo(Voyage.NONE);
        assertThat(handlingEvent.cargo()).isEqualTo(cargo);
        assertThat(handlingEvent.completionTime()).isEqualTo(Instant.ofEpochMilli(100));
        assertThat(handlingEvent.registrationTime().isBefore(Instant.ofEpochMilli(System.currentTimeMillis() + 1))).isTrue();
    }

	@Test
    void testCreateHandlingEventUnknownLocation() throws Exception {
        when(cargoRepository.find(trackingId)).thenReturn(cargo);

        UnLocode invalid = new UnLocode("NOEXT");
        try {
            factory.createHandlingEvent(
                    Instant.now(), Instant.ofEpochMilli(100), trackingId, CM001.voyageNumber(), invalid, Type.LOAD
            );
            fail("Expected UnknownLocationException");
        } catch (UnknownLocationException expected) {
        }
    }

	@Test
    void testCreateHandlingEventUnknownCarrierMovement() throws Exception {
        when(cargoRepository.find(trackingId)).thenReturn(cargo);

        try {
            VoyageNumber invalid = new VoyageNumber("XXX");
            factory.createHandlingEvent(
                    Instant.now(), Instant.ofEpochMilli(100), trackingId, invalid, STOCKHOLM.unLocode(), Type.LOAD
            );
            fail("Expected UnknownVoyageException");
        } catch (UnknownVoyageException expected) {
        }
    }

	@Test
    void testCreateHandlingEventUnknownTrackingId() throws Exception {
        when(cargoRepository.find(trackingId)).thenReturn(null);

        try {
            factory.createHandlingEvent(
                    Instant.now(), Instant.ofEpochMilli(100), trackingId, CM001.voyageNumber(), STOCKHOLM.unLocode(), Type.LOAD
            );
            fail("Expected UnknownCargoException");
        } catch (UnknownCargoException expected) {
        }
    }

}
