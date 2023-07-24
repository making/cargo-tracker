package lol.maki.cargotracker.application;

import lol.maki.cargotracker.domain.model.cargo.Cargo;
import lol.maki.cargotracker.domain.model.cargo.CargoRepository;
import lol.maki.cargotracker.domain.model.cargo.RouteSpecification;
import lol.maki.cargotracker.domain.model.cargo.TrackingId;
import lol.maki.cargotracker.domain.model.handling.HandlingEventRepository;
import lol.maki.cargotracker.domain.model.location.LocationRepository;
import lol.maki.cargotracker.infrastructure.sampledata.SampleLocations;
import lol.maki.cargotracker.infrastructure.sampledata.SampleVoyages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import lol.maki.cargotracker.domain.model.handling.HandlingEvent;
import lol.maki.cargotracker.domain.model.handling.HandlingEventFactory;
import lol.maki.cargotracker.domain.model.voyage.VoyageRepository;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

class HandlingEventServiceTest {

	private HandlingEventService service;

	private ApplicationEvents applicationEvents;

	private CargoRepository cargoRepository;

	private VoyageRepository voyageRepository;

	private HandlingEventRepository handlingEventRepository;

	private LocationRepository locationRepository;

	private final Cargo cargo = new Cargo(new TrackingId("ABC"),
			new RouteSpecification(SampleLocations.HAMBURG, SampleLocations.TOKYO, Instant.now()));

	@BeforeEach
	void setUp() {
		cargoRepository = mock(CargoRepository.class);
		voyageRepository = mock(VoyageRepository.class);
		handlingEventRepository = mock(HandlingEventRepository.class);
		locationRepository = mock(LocationRepository.class);
		applicationEvents = Mockito.mock(ApplicationEvents.class);

		HandlingEventFactory handlingEventFactory = new HandlingEventFactory(cargoRepository, voyageRepository,
				locationRepository);
		service = new HandlingEventService(handlingEventRepository, applicationEvents, handlingEventFactory);
	}

	@Test
    void testRegisterEvent() throws Exception {
        when(cargoRepository.find(cargo.trackingId())).thenReturn(cargo);
        when(voyageRepository.find(SampleVoyages.CM001.voyageNumber())).thenReturn(SampleVoyages.CM001);
        when(locationRepository.find(SampleLocations.STOCKHOLM.unLocode())).thenReturn(SampleLocations.STOCKHOLM);

        service.registerHandlingEvent(Instant.now(), cargo.trackingId(), SampleVoyages.CM001.voyageNumber(), SampleLocations.STOCKHOLM.unLocode(), HandlingEvent.Type.LOAD);
        verify(handlingEventRepository, times(1)).store(isA(HandlingEvent.class));
        verify(applicationEvents, times(1)).cargoWasHandled(isA(HandlingEvent.class));
    }

}
