package lol.maki.cargotracker.application;

import lol.maki.cargotracker.domain.model.cargo.Cargo;
import lol.maki.cargotracker.domain.model.cargo.CargoFactory;
import lol.maki.cargotracker.domain.model.cargo.CargoRepository;
import lol.maki.cargotracker.domain.model.cargo.TrackingId;
import lol.maki.cargotracker.domain.model.location.LocationRepository;
import lol.maki.cargotracker.domain.model.location.UnLocode;
import lol.maki.cargotracker.domain.service.RoutingService;
import lol.maki.cargotracker.infrastructure.sampledata.SampleLocations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

class BookingServiceTest {

	BookingService bookingService;

	CargoRepository cargoRepository;

	LocationRepository locationRepository;

	RoutingService routingService;

	CargoFactory cargoFactory;

	@BeforeEach
	void setUp() {
		cargoRepository = mock(CargoRepository.class);
		locationRepository = mock(LocationRepository.class);
		routingService = mock(RoutingService.class);
		cargoFactory = new CargoFactory(locationRepository, cargoRepository);
		bookingService = new BookingService(cargoRepository, locationRepository, routingService, cargoFactory);
	}

	@Test
	void testRegisterNew() {
		TrackingId expectedTrackingId = new TrackingId("TRK1");
		UnLocode fromUnlocode = new UnLocode("USCHI");
		UnLocode toUnlocode = new UnLocode("SESTO");

		when(cargoRepository.nextTrackingId()).thenReturn(expectedTrackingId);
		when(locationRepository.find(fromUnlocode)).thenReturn(SampleLocations.CHICAGO);
		when(locationRepository.find(toUnlocode)).thenReturn(SampleLocations.STOCKHOLM);

		TrackingId trackingId = bookingService.bookNewCargo(fromUnlocode, toUnlocode, Instant.now());
		assertThat(trackingId).isEqualTo(expectedTrackingId);
		verify(cargoRepository, times(1)).store(isA(Cargo.class));
		verify(locationRepository, times(2)).find(any(UnLocode.class));
	}

}
