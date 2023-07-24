package lol.maki.cargotracker.domain.model.cargo;

import lol.maki.cargotracker.infrastructure.sampledata.SampleLocations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import lol.maki.cargotracker.domain.model.handling.HandlingEvent;
import lol.maki.cargotracker.domain.model.voyage.CarrierMovement;
import lol.maki.cargotracker.domain.model.voyage.Voyage;
import lol.maki.cargotracker.domain.model.voyage.VoyageNumber;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItineraryTest {

	private final CarrierMovement abc = new CarrierMovement(SampleLocations.SHANGHAI, SampleLocations.ROTTERDAM,
			Instant.now(), Instant.now());

	private final CarrierMovement def = new CarrierMovement(SampleLocations.ROTTERDAM, SampleLocations.GOTHENBURG,
			Instant.now(), Instant.now());

	private final CarrierMovement ghi = new CarrierMovement(SampleLocations.ROTTERDAM, SampleLocations.NEWYORK,
			Instant.now(), Instant.now());

	private final CarrierMovement jkl = new CarrierMovement(SampleLocations.SHANGHAI, SampleLocations.HELSINKI,
			Instant.now(), Instant.now());

	Voyage voyage, wrongVoyage;

	@BeforeEach
	void setUp() {
		voyage = new Voyage.Builder(new VoyageNumber("0123"), SampleLocations.SHANGHAI)
			.addMovement(SampleLocations.ROTTERDAM, Instant.now(), Instant.now())
			.addMovement(SampleLocations.GOTHENBURG, Instant.now(), Instant.now())
			.build();

		wrongVoyage = new Voyage.Builder(new VoyageNumber("666"), SampleLocations.NEWYORK)
			.addMovement(SampleLocations.STOCKHOLM, Instant.now(), Instant.now())
			.addMovement(SampleLocations.HELSINKI, Instant.now(), Instant.now())
			.build();
	}

	@Test
	void testCargoOnTrack() {

		TrackingId trackingId = new TrackingId("CARGO1");
		RouteSpecification routeSpecification = new RouteSpecification(SampleLocations.SHANGHAI,
				SampleLocations.GOTHENBURG, Instant.now());
		Cargo cargo = new Cargo(trackingId, routeSpecification);

		Itinerary itinerary = new Itinerary(List.of(
				new Leg(voyage, SampleLocations.SHANGHAI, SampleLocations.ROTTERDAM, Instant.now(), Instant.now()),
				new Leg(voyage, SampleLocations.ROTTERDAM, SampleLocations.GOTHENBURG, Instant.now(), Instant.now())));

		// Happy path
		HandlingEvent event = new HandlingEvent(cargo, Instant.now(), Instant.now(), HandlingEvent.Type.RECEIVE,
				SampleLocations.SHANGHAI);
		assertThat(itinerary.isExpected(event)).isTrue();

		event = new HandlingEvent(cargo, Instant.now(), Instant.now(), HandlingEvent.Type.LOAD,
				SampleLocations.SHANGHAI, voyage);
		assertThat(itinerary.isExpected(event)).isTrue();

		event = new HandlingEvent(cargo, Instant.now(), Instant.now(), HandlingEvent.Type.UNLOAD,
				SampleLocations.ROTTERDAM, voyage);
		assertThat(itinerary.isExpected(event)).isTrue();

		event = new HandlingEvent(cargo, Instant.now(), Instant.now(), HandlingEvent.Type.LOAD,
				SampleLocations.ROTTERDAM, voyage);
		assertThat(itinerary.isExpected(event)).isTrue();

		event = new HandlingEvent(cargo, Instant.now(), Instant.now(), HandlingEvent.Type.UNLOAD,
				SampleLocations.GOTHENBURG, voyage);
		assertThat(itinerary.isExpected(event)).isTrue();

		event = new HandlingEvent(cargo, Instant.now(), Instant.now(), HandlingEvent.Type.CLAIM,
				SampleLocations.GOTHENBURG);
		assertThat(itinerary.isExpected(event)).isTrue();

		// Customs event changes nothing
		event = new HandlingEvent(cargo, Instant.now(), Instant.now(), HandlingEvent.Type.CUSTOMS,
				SampleLocations.GOTHENBURG);
		assertThat(itinerary.isExpected(event)).isTrue();

		// Received at the wrong location
		event = new HandlingEvent(cargo, Instant.now(), Instant.now(), HandlingEvent.Type.RECEIVE,
				SampleLocations.HANGZHOU);
		assertThat(itinerary.isExpected(event)).isFalse();

		// Loaded to onto the wrong ship, correct location
		event = new HandlingEvent(cargo, Instant.now(), Instant.now(), HandlingEvent.Type.LOAD,
				SampleLocations.ROTTERDAM, wrongVoyage);
		assertThat(itinerary.isExpected(event)).isFalse();

		// Unloaded from the wrong ship in the wrong location
		event = new HandlingEvent(cargo, Instant.now(), Instant.now(), HandlingEvent.Type.UNLOAD,
				SampleLocations.HELSINKI, wrongVoyage);
		assertThat(itinerary.isExpected(event)).isFalse();

		event = new HandlingEvent(cargo, Instant.now(), Instant.now(), HandlingEvent.Type.CLAIM,
				SampleLocations.ROTTERDAM);
		assertThat(itinerary.isExpected(event)).isFalse();

	}

	@Test
	void testNextExpectedEvent() {

	}

	@Test
	void shouldNotAllowItineraryWithEmptyListOfLegs() {
		assertThatThrownBy(() -> new Itinerary(List.of())).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void shouldNotAllowItineraryWithNullListOfLegs() {
		assertThatThrownBy(() -> new Itinerary(null)).isInstanceOf(NullPointerException.class);
	}

}