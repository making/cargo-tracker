package lol.maki.cargotracker.interfaces.tracking;

import lol.maki.cargotracker.domain.model.cargo.Cargo;
import lol.maki.cargotracker.domain.model.cargo.RouteSpecification;
import lol.maki.cargotracker.domain.model.cargo.TrackingId;
import lol.maki.cargotracker.domain.model.handling.HandlingEvent;
import lol.maki.cargotracker.domain.model.handling.HandlingHistory;
import lol.maki.cargotracker.infrastructure.sampledata.SampleLocations;
import lol.maki.cargotracker.infrastructure.sampledata.SampleVoyages;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticApplicationContext;
import lol.maki.cargotracker.interfaces.tracking.web.CargoTrackingViewAdapter;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class CargoTrackingViewAdapterTest {

	@Test
	void testCreate() {
		Cargo cargo = new Cargo(new TrackingId("XYZ"),
				new RouteSpecification(SampleLocations.HANGZHOU, SampleLocations.HELSINKI, Instant.now()));

		List<HandlingEvent> events = new ArrayList<HandlingEvent>();
		events.add(new HandlingEvent(cargo, Instant.ofEpochMilli(1), Instant.ofEpochMilli(2),
				HandlingEvent.Type.RECEIVE, SampleLocations.HANGZHOU));

		events.add(new HandlingEvent(cargo, Instant.ofEpochMilli(3), Instant.ofEpochMilli(4), HandlingEvent.Type.LOAD,
				SampleLocations.HANGZHOU, SampleVoyages.CM001));
		events.add(new HandlingEvent(cargo, Instant.ofEpochMilli(5), Instant.ofEpochMilli(6), HandlingEvent.Type.UNLOAD,
				SampleLocations.HELSINKI, SampleVoyages.CM001));

		cargo.deriveDeliveryProgress(new HandlingHistory(events));

		StaticApplicationContext applicationContext = new StaticApplicationContext();
		applicationContext.addMessage("cargo.status.IN_PORT", Locale.GERMAN, "In port {0}");
		applicationContext.refresh();

		CargoTrackingViewAdapter adapter = new CargoTrackingViewAdapter(cargo, applicationContext, Locale.GERMAN,
				events, TimeZone.getTimeZone("Europe/Stockholm"));

		assertThat(adapter.getTrackingId()).isEqualTo("XYZ");
		assertThat(adapter.getOrigin()).isEqualTo("Hangzhou");
		assertThat(adapter.getDestination()).isEqualTo("Helsinki");
		assertThat(adapter.getStatusText()).isEqualTo("In port Helsinki");

		Iterator<CargoTrackingViewAdapter.HandlingEventViewAdapter> it = adapter.getEvents().iterator();

		CargoTrackingViewAdapter.HandlingEventViewAdapter event = it.next();
		assertThat(event.getType()).isEqualTo("RECEIVE");
		assertThat(event.getLocation()).isEqualTo("Hangzhou");
		assertThat(event.getTime()).isEqualTo("1970-01-01 01:00");
		assertThat(event.getVoyageNumber()).isEqualTo("");
		assertThat(event.isExpected()).isTrue();

		event = it.next();
		assertThat(event.getType()).isEqualTo("LOAD");
		assertThat(event.getLocation()).isEqualTo("Hangzhou");
		assertThat(event.getTime()).isEqualTo("1970-01-01 01:00");
		assertThat(event.getVoyageNumber()).isEqualTo("CM001");
		assertThat(event.isExpected()).isTrue();

		event = it.next();
		assertThat(event.getType()).isEqualTo("UNLOAD");
		assertThat(event.getLocation()).isEqualTo("Helsinki");
		assertThat(event.getTime()).isEqualTo("1970-01-01 01:00");
		assertThat(event.getVoyageNumber()).isEqualTo("CM001");
		assertThat(event.isExpected()).isTrue();
	}

}
