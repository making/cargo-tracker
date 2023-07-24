package lol.maki.cargotracker.domain.model.cargo;

import lol.maki.cargotracker.infrastructure.sampledata.SampleLocations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import lol.maki.cargotracker.application.util.DateUtils;
import lol.maki.cargotracker.domain.model.handling.HandlingEvent;
import lol.maki.cargotracker.domain.model.handling.HandlingHistory;
import lol.maki.cargotracker.domain.model.location.Location;
import lol.maki.cargotracker.domain.model.voyage.Voyage;
import lol.maki.cargotracker.domain.model.voyage.VoyageNumber;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CargoTest {

	private List<HandlingEvent> events;

	private Voyage voyage;

	@BeforeEach
	void setUp() {
		events = new ArrayList<HandlingEvent>();

		voyage = new Voyage.Builder(new VoyageNumber("0123"), SampleLocations.STOCKHOLM)
			.addMovement(SampleLocations.HAMBURG, Instant.now(), Instant.now())
			.addMovement(SampleLocations.HONGKONG, Instant.now(), Instant.now())
			.addMovement(SampleLocations.MELBOURNE, Instant.now(), Instant.now())
			.build();
	}

	@Test
	void testConstruction() {
		final TrackingId trackingId = new TrackingId("XYZ");
		final Instant arrivalDeadline = DateUtils.toDate("2009-03-13");
		final RouteSpecification routeSpecification = new RouteSpecification(SampleLocations.STOCKHOLM,
				SampleLocations.MELBOURNE, arrivalDeadline);

		final Cargo cargo = new Cargo(trackingId, routeSpecification);

		assertThat(cargo.delivery().routingStatus()).isEqualTo(RoutingStatus.NOT_ROUTED);
		assertThat(cargo.delivery().transportStatus()).isEqualTo(TransportStatus.NOT_RECEIVED);
		assertThat(cargo.delivery().lastKnownLocation()).isEqualTo(Location.UNKNOWN);
		assertThat(cargo.delivery().currentVoyage()).isEqualTo(Voyage.NONE);
	}

	@Test
	void testRoutingStatus() {
		final Cargo cargo = new Cargo(new TrackingId("XYZ"),
				new RouteSpecification(SampleLocations.STOCKHOLM, SampleLocations.MELBOURNE, Instant.now()));
		final Itinerary good = new Itinerary();
		final Itinerary bad = new Itinerary();
		final RouteSpecification acceptOnlyGood = new RouteSpecification(cargo.origin(),
				cargo.routeSpecification().destination(), Instant.now()) {
			@Override
			public boolean isSatisfiedBy(Itinerary itinerary) {
				return itinerary == good;
			}
		};

		cargo.specifyNewRoute(acceptOnlyGood);

		assertThat(cargo.delivery().routingStatus()).isEqualTo(RoutingStatus.NOT_ROUTED);

		cargo.assignToRoute(bad);
		assertThat(cargo.delivery().routingStatus()).isEqualTo(RoutingStatus.MISROUTED);

		cargo.assignToRoute(good);
		assertThat(cargo.delivery().routingStatus()).isEqualTo(RoutingStatus.ROUTED);
	}

	@Test
	void testlastKnownLocationUnknownWhenNoEvents() {
		Cargo cargo = new Cargo(new TrackingId("XYZ"),
				new RouteSpecification(SampleLocations.STOCKHOLM, SampleLocations.MELBOURNE, Instant.now()));

		assertThat(cargo.delivery().lastKnownLocation()).isEqualTo(Location.UNKNOWN);
	}

	@Test
	void testlastKnownLocationReceived() throws Exception {
		Cargo cargo = populateCargoReceivedStockholm();

		assertThat(cargo.delivery().lastKnownLocation()).isEqualTo(SampleLocations.STOCKHOLM);
	}

	@Test
	void testlastKnownLocationClaimed() throws Exception {
		Cargo cargo = populateCargoClaimedMelbourne();

		assertThat(cargo.delivery().lastKnownLocation()).isEqualTo(SampleLocations.MELBOURNE);
	}

	@Test
	void testlastKnownLocationUnloaded() throws Exception {
		Cargo cargo = populateCargoOffHongKong();

		assertThat(cargo.delivery().lastKnownLocation()).isEqualTo(SampleLocations.HONGKONG);
	}

	@Test
	void testlastKnownLocationloaded() throws Exception {
		Cargo cargo = populateCargoOnHamburg();

		assertThat(cargo.delivery().lastKnownLocation()).isEqualTo(SampleLocations.HAMBURG);
	}

	@Test
	void testEquality() {
		RouteSpecification spec1 = new RouteSpecification(SampleLocations.STOCKHOLM, SampleLocations.HONGKONG,
				Instant.now());
		RouteSpecification spec2 = new RouteSpecification(SampleLocations.STOCKHOLM, SampleLocations.MELBOURNE,
				Instant.now());
		Cargo c1 = new Cargo(new TrackingId("ABC"), spec1);
		Cargo c2 = new Cargo(new TrackingId("CBA"), spec1);
		Cargo c3 = new Cargo(new TrackingId("ABC"), spec2);
		Cargo c4 = new Cargo(new TrackingId("ABC"), spec1);

		assertThat(c1.equals(c4)).as("Cargos should be equal when TrackingIDs are equal").isTrue();
		assertThat(c1.equals(c3)).as("Cargos should be equal when TrackingIDs are equal").isTrue();
		assertThat(c3.equals(c4)).as("Cargos should be equal when TrackingIDs are equal").isTrue();
		assertThat(c1.equals(c2)).as("Cargos are not equal when TrackingID differ").isFalse();
	}

	@Test
	void testIsUnloadedAtFinalDestination() {
		Cargo cargo = setUpCargoWithItinerary(SampleLocations.HANGZHOU, SampleLocations.TOKYO, SampleLocations.NEWYORK);
		assertThat(cargo.delivery().isUnloadedAtDestination()).isFalse();

		// Adding an event unrelated to unloading at final destination
		events.add(new HandlingEvent(cargo, Instant.ofEpochMilli(10), Instant.now(), HandlingEvent.Type.RECEIVE,
				SampleLocations.HANGZHOU));
		cargo.deriveDeliveryProgress(new HandlingHistory(events));
		assertThat(cargo.delivery().isUnloadedAtDestination()).isFalse();

		Voyage voyage = new Voyage.Builder(new VoyageNumber("0123"), SampleLocations.HANGZHOU)
			.addMovement(SampleLocations.NEWYORK, Instant.now(), Instant.now())
			.build();

		// Adding an unload event, but not at the final destination
		events.add(new HandlingEvent(cargo, Instant.ofEpochSecond(20), Instant.now(), HandlingEvent.Type.UNLOAD,
				SampleLocations.TOKYO, voyage));
		cargo.deriveDeliveryProgress(new HandlingHistory(events));
		assertThat(cargo.delivery().isUnloadedAtDestination()).isFalse();

		// Adding an event in the final destination, but not unload
		events.add(new HandlingEvent(cargo, Instant.ofEpochSecond(30), Instant.now(), HandlingEvent.Type.CUSTOMS,
				SampleLocations.NEWYORK));
		cargo.deriveDeliveryProgress(new HandlingHistory(events));
		assertThat(cargo.delivery().isUnloadedAtDestination()).isFalse();

		// Finally, cargo is unloaded at final destination
		events.add(new HandlingEvent(cargo, Instant.ofEpochSecond(40), Instant.now(), HandlingEvent.Type.UNLOAD,
				SampleLocations.NEWYORK, voyage));
		cargo.deriveDeliveryProgress(new HandlingHistory(events));
		assertThat(cargo.delivery().isUnloadedAtDestination()).isTrue();
	}

	// TODO: Generate test data some better way
	private Cargo populateCargoReceivedStockholm() throws Exception {
		final Cargo cargo = new Cargo(new TrackingId("XYZ"),
				new RouteSpecification(SampleLocations.STOCKHOLM, SampleLocations.MELBOURNE, Instant.now()));

		HandlingEvent he = new HandlingEvent(cargo, getDate("2007-12-01"), Instant.now(), HandlingEvent.Type.RECEIVE,
				SampleLocations.STOCKHOLM);
		events.add(he);
		cargo.deriveDeliveryProgress(new HandlingHistory(events));

		return cargo;
	}

	private Cargo populateCargoClaimedMelbourne() throws Exception {
		final Cargo cargo = populateCargoOffMelbourne();

		events.add(new HandlingEvent(cargo, getDate("2007-12-09"), Instant.now(), HandlingEvent.Type.CLAIM,
				SampleLocations.MELBOURNE));
		cargo.deriveDeliveryProgress(new HandlingHistory(events));

		return cargo;
	}

	private Cargo populateCargoOffHongKong() throws Exception {
		final Cargo cargo = new Cargo(new TrackingId("XYZ"),
				new RouteSpecification(SampleLocations.STOCKHOLM, SampleLocations.MELBOURNE, Instant.now()));

		events.add(new HandlingEvent(cargo, getDate("2007-12-01"), Instant.now(), HandlingEvent.Type.LOAD,
				SampleLocations.STOCKHOLM, voyage));
		events.add(new HandlingEvent(cargo, getDate("2007-12-02"), Instant.now(), HandlingEvent.Type.UNLOAD,
				SampleLocations.HAMBURG, voyage));

		events.add(new HandlingEvent(cargo, getDate("2007-12-03"), Instant.now(), HandlingEvent.Type.LOAD,
				SampleLocations.HAMBURG, voyage));
		events.add(new HandlingEvent(cargo, getDate("2007-12-04"), Instant.now(), HandlingEvent.Type.UNLOAD,
				SampleLocations.HONGKONG, voyage));

		cargo.deriveDeliveryProgress(new HandlingHistory(events));
		return cargo;
	}

	private Cargo populateCargoOnHamburg() throws Exception {
		final Cargo cargo = new Cargo(new TrackingId("XYZ"),
				new RouteSpecification(SampleLocations.STOCKHOLM, SampleLocations.MELBOURNE, Instant.now()));

		events.add(new HandlingEvent(cargo, getDate("2007-12-01"), Instant.now(), HandlingEvent.Type.LOAD,
				SampleLocations.STOCKHOLM, voyage));
		events.add(new HandlingEvent(cargo, getDate("2007-12-02"), Instant.now(), HandlingEvent.Type.UNLOAD,
				SampleLocations.HAMBURG, voyage));
		events.add(new HandlingEvent(cargo, getDate("2007-12-03"), Instant.now(), HandlingEvent.Type.LOAD,
				SampleLocations.HAMBURG, voyage));

		cargo.deriveDeliveryProgress(new HandlingHistory(events));
		return cargo;
	}

	private Cargo populateCargoOffMelbourne() throws Exception {
		final Cargo cargo = new Cargo(new TrackingId("XYZ"),
				new RouteSpecification(SampleLocations.STOCKHOLM, SampleLocations.MELBOURNE, Instant.now()));

		events.add(new HandlingEvent(cargo, getDate("2007-12-01"), Instant.now(), HandlingEvent.Type.LOAD,
				SampleLocations.STOCKHOLM, voyage));
		events.add(new HandlingEvent(cargo, getDate("2007-12-02"), Instant.now(), HandlingEvent.Type.UNLOAD,
				SampleLocations.HAMBURG, voyage));

		events.add(new HandlingEvent(cargo, getDate("2007-12-03"), Instant.now(), HandlingEvent.Type.LOAD,
				SampleLocations.HAMBURG, voyage));
		events.add(new HandlingEvent(cargo, getDate("2007-12-04"), Instant.now(), HandlingEvent.Type.UNLOAD,
				SampleLocations.HONGKONG, voyage));

		events.add(new HandlingEvent(cargo, getDate("2007-12-05"), Instant.now(), HandlingEvent.Type.LOAD,
				SampleLocations.HONGKONG, voyage));
		events.add(new HandlingEvent(cargo, getDate("2007-12-07"), Instant.now(), HandlingEvent.Type.UNLOAD,
				SampleLocations.MELBOURNE, voyage));

		cargo.deriveDeliveryProgress(new HandlingHistory(events));
		return cargo;
	}

	private Cargo populateCargoOnHongKong() throws Exception {
		final Cargo cargo = new Cargo(new TrackingId("XYZ"),
				new RouteSpecification(SampleLocations.STOCKHOLM, SampleLocations.MELBOURNE, Instant.now()));

		events.add(new HandlingEvent(cargo, getDate("2007-12-01"), Instant.now(), HandlingEvent.Type.LOAD,
				SampleLocations.STOCKHOLM, voyage));
		events.add(new HandlingEvent(cargo, getDate("2007-12-02"), Instant.now(), HandlingEvent.Type.UNLOAD,
				SampleLocations.HAMBURG, voyage));

		events.add(new HandlingEvent(cargo, getDate("2007-12-03"), Instant.now(), HandlingEvent.Type.LOAD,
				SampleLocations.HAMBURG, voyage));
		events.add(new HandlingEvent(cargo, getDate("2007-12-04"), Instant.now(), HandlingEvent.Type.UNLOAD,
				SampleLocations.HONGKONG, voyage));

		events.add(new HandlingEvent(cargo, getDate("2007-12-05"), Instant.now(), HandlingEvent.Type.LOAD,
				SampleLocations.HONGKONG, voyage));

		cargo.deriveDeliveryProgress(new HandlingHistory(events));
		return cargo;
	}

	@Test
	void testIsMisdirected() {
		// A cargo with no itinerary is not misdirected
		Cargo cargo = new Cargo(new TrackingId("TRKID"),
				new RouteSpecification(SampleLocations.SHANGHAI, SampleLocations.GOTHENBURG, Instant.now()));
		assertThat(cargo.delivery().isMisdirected()).isFalse();

		cargo = setUpCargoWithItinerary(SampleLocations.SHANGHAI, SampleLocations.ROTTERDAM,
				SampleLocations.GOTHENBURG);

		// A cargo with no handling events is not misdirected
		assertThat(cargo.delivery().isMisdirected()).isFalse();

		Collection<HandlingEvent> handlingEvents = new ArrayList<HandlingEvent>();

		// Happy path
		handlingEvents.add(new HandlingEvent(cargo, Instant.ofEpochSecond(10), Instant.ofEpochSecond(20),
				HandlingEvent.Type.RECEIVE, SampleLocations.SHANGHAI));
		handlingEvents.add(new HandlingEvent(cargo, Instant.ofEpochSecond(30), Instant.ofEpochSecond(40),
				HandlingEvent.Type.LOAD, SampleLocations.SHANGHAI, voyage));
		handlingEvents.add(new HandlingEvent(cargo, Instant.ofEpochSecond(50), Instant.ofEpochSecond(60),
				HandlingEvent.Type.UNLOAD, SampleLocations.ROTTERDAM, voyage));
		handlingEvents.add(new HandlingEvent(cargo, Instant.ofEpochSecond(70), Instant.ofEpochSecond(80),
				HandlingEvent.Type.LOAD, SampleLocations.ROTTERDAM, voyage));
		handlingEvents.add(new HandlingEvent(cargo, Instant.ofEpochSecond(90), Instant.ofEpochSecond(100),
				HandlingEvent.Type.UNLOAD, SampleLocations.GOTHENBURG, voyage));
		handlingEvents.add(new HandlingEvent(cargo, Instant.ofEpochSecond(110), Instant.ofEpochSecond(120),
				HandlingEvent.Type.CLAIM, SampleLocations.GOTHENBURG));
		handlingEvents.add(new HandlingEvent(cargo, Instant.ofEpochSecond(130), Instant.ofEpochSecond(140),
				HandlingEvent.Type.CUSTOMS, SampleLocations.GOTHENBURG));

		events.addAll(handlingEvents);
		cargo.deriveDeliveryProgress(new HandlingHistory(events));
		assertThat(cargo.delivery().isMisdirected()).isFalse();

		// Try a couple of failing ones

		cargo = setUpCargoWithItinerary(SampleLocations.SHANGHAI, SampleLocations.ROTTERDAM,
				SampleLocations.GOTHENBURG);
		handlingEvents = new ArrayList<HandlingEvent>();

		handlingEvents.add(new HandlingEvent(cargo, Instant.now(), Instant.now(), HandlingEvent.Type.RECEIVE,
				SampleLocations.HANGZHOU));
		events.addAll(handlingEvents);
		cargo.deriveDeliveryProgress(new HandlingHistory(events));

		assertThat(cargo.delivery().isMisdirected()).isTrue();

		cargo = setUpCargoWithItinerary(SampleLocations.SHANGHAI, SampleLocations.ROTTERDAM,
				SampleLocations.GOTHENBURG);
		handlingEvents = new ArrayList<HandlingEvent>();

		handlingEvents.add(new HandlingEvent(cargo, Instant.ofEpochSecond(10), Instant.ofEpochSecond(20),
				HandlingEvent.Type.RECEIVE, SampleLocations.SHANGHAI));
		handlingEvents.add(new HandlingEvent(cargo, Instant.ofEpochSecond(30), Instant.ofEpochSecond(40),
				HandlingEvent.Type.LOAD, SampleLocations.SHANGHAI, voyage));
		handlingEvents.add(new HandlingEvent(cargo, Instant.ofEpochSecond(50), Instant.ofEpochSecond(60),
				HandlingEvent.Type.UNLOAD, SampleLocations.ROTTERDAM, voyage));
		handlingEvents.add(new HandlingEvent(cargo, Instant.ofEpochSecond(70), Instant.ofEpochSecond(80),
				HandlingEvent.Type.LOAD, SampleLocations.ROTTERDAM, voyage));

		events.addAll(handlingEvents);
		cargo.deriveDeliveryProgress(new HandlingHistory(events));

		assertThat(cargo.delivery().isMisdirected()).isTrue();

		cargo = setUpCargoWithItinerary(SampleLocations.SHANGHAI, SampleLocations.ROTTERDAM,
				SampleLocations.GOTHENBURG);
		handlingEvents = new ArrayList<HandlingEvent>();

		handlingEvents.add(new HandlingEvent(cargo, Instant.ofEpochSecond(10), Instant.ofEpochSecond(20),
				HandlingEvent.Type.RECEIVE, SampleLocations.SHANGHAI));
		handlingEvents.add(new HandlingEvent(cargo, Instant.ofEpochSecond(30), Instant.ofEpochSecond(40),
				HandlingEvent.Type.LOAD, SampleLocations.SHANGHAI, voyage));
		handlingEvents.add(new HandlingEvent(cargo, Instant.ofEpochSecond(50), Instant.ofEpochSecond(60),
				HandlingEvent.Type.UNLOAD, SampleLocations.ROTTERDAM, voyage));
		handlingEvents.add(new HandlingEvent(cargo, Instant.now(), Instant.now(), HandlingEvent.Type.CLAIM,
				SampleLocations.ROTTERDAM));

		events.addAll(handlingEvents);
		cargo.deriveDeliveryProgress(new HandlingHistory(events));

		assertThat(cargo.delivery().isMisdirected()).isTrue();
	}

	private Cargo setUpCargoWithItinerary(Location origin, Location midpoint, Location destination) {
		Cargo cargo = new Cargo(new TrackingId("CARGO1"), new RouteSpecification(origin, destination, Instant.now()));

		Itinerary itinerary = new Itinerary(List.of(new Leg(voyage, origin, midpoint, Instant.now(), Instant.now()),
				new Leg(voyage, midpoint, destination, Instant.now(), Instant.now())));

		cargo.assignToRoute(itinerary);
		return cargo;
	}

	/**
	 * Parse an ISO 8601 (YYYY-MM-DD) String to Date
	 * @param isoFormat String to parse.
	 * @return Created date instance.
	 * @throws DateTimeParseException Thrown if parsing fails.
	 */
	private Instant getDate(String isoFormat) throws DateTimeParseException {
		return LocalDate.parse(isoFormat).atStartOfDay().toInstant(ZoneOffset.UTC);
	}

}
