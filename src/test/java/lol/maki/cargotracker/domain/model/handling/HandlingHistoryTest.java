package lol.maki.cargotracker.domain.model.handling;

import lol.maki.cargotracker.domain.model.cargo.RouteSpecification;
import lol.maki.cargotracker.domain.model.cargo.TrackingId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import lol.maki.cargotracker.domain.model.cargo.Cargo;
import lol.maki.cargotracker.domain.model.voyage.Voyage;
import lol.maki.cargotracker.domain.model.voyage.VoyageNumber;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static lol.maki.cargotracker.application.util.DateUtils.toDate;
import static lol.maki.cargotracker.infrastructure.sampledata.SampleLocations.*;

class HandlingHistoryTest {

	Cargo cargo;

	Voyage voyage;

	HandlingEvent event1;

	HandlingEvent event1duplicate;

	HandlingEvent event2;

	HandlingHistory handlingHistory;

	@BeforeEach
	void setUp() {
		cargo = new Cargo(new TrackingId("ABC"), new RouteSpecification(SHANGHAI, DALLAS, toDate("2009-04-01")));
		voyage = new Voyage.Builder(new VoyageNumber("X25"), HONGKONG)
			.addMovement(SHANGHAI, Instant.now(), Instant.now())
			.addMovement(DALLAS, Instant.now(), Instant.now())
			.build();
		event1 = new HandlingEvent(cargo, toDate("2009-03-05"), Instant.ofEpochMilli(100), HandlingEvent.Type.LOAD,
				SHANGHAI, voyage);
		event1duplicate = new HandlingEvent(cargo, toDate("2009-03-05"), Instant.ofEpochMilli(200),
				HandlingEvent.Type.LOAD, SHANGHAI, voyage);
		event2 = new HandlingEvent(cargo, toDate("2009-03-10"), Instant.ofEpochMilli(150), HandlingEvent.Type.UNLOAD,
				DALLAS, voyage);

		handlingHistory = new HandlingHistory(List.of(event2, event1, event1duplicate));
	}

	@Test
	void testDistinctEventsByCompletionTime() {
		assertThat(handlingHistory.distinctEventsByCompletionTime()).isEqualTo(List.of(event1, event2));
	}

	@Test
	void testMostRecentlyCompletedEvent() {
		assertThat(handlingHistory.mostRecentlyCompletedEvent()).isEqualTo(event2);
	}

}
