package lol.maki.cargotracker.interfaces.booking.facade.internal.assembler;

import lol.maki.cargotracker.domain.model.cargo.*;
import lol.maki.cargotracker.domain.model.location.Location;
import lol.maki.cargotracker.infrastructure.sampledata.SampleLocations;
import lol.maki.cargotracker.infrastructure.sampledata.SampleVoyages;
import lol.maki.cargotracker.interfaces.booking.facade.CargoRoutingDTO;
import lol.maki.cargotracker.interfaces.booking.facade.CargoRoutingDTOAssembler;
import lol.maki.cargotracker.interfaces.booking.facade.LegDTO;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CargoRoutingDTOAssemblerTest {

	@Test
	void testToDTO() {
		final CargoRoutingDTOAssembler assembler = new CargoRoutingDTOAssembler();

		final Location origin = SampleLocations.STOCKHOLM;
		final Location destination = SampleLocations.MELBOURNE;
		final Cargo cargo = new Cargo(new TrackingId("XYZ"),
				new RouteSpecification(origin, destination, Instant.now()));

		final Itinerary itinerary = new Itinerary(List.of(
				new Leg(SampleVoyages.CM001, origin, SampleLocations.SHANGHAI, Instant.now(), Instant.now()),
				new Leg(SampleVoyages.CM001, SampleLocations.ROTTERDAM, destination, Instant.now(), Instant.now())));

		cargo.assignToRoute(itinerary);

		final CargoRoutingDTO dto = assembler.toDTO(cargo);

		assertThat(dto.legs()).hasSize(2);

		LegDTO legDTO = dto.legs().get(0);
		assertThat(legDTO.voyageNumber()).isEqualTo("CM001");
		assertThat(legDTO.from()).isEqualTo("SESTO");
		assertThat(legDTO.to()).isEqualTo("CNSHA");

		legDTO = dto.legs().get(1);
		assertThat(legDTO.voyageNumber()).isEqualTo("CM001");
		assertThat(legDTO.from()).isEqualTo("NLRTM");
		assertThat(legDTO.to()).isEqualTo("AUMEL");
	}

	@Test
	void testToDTO_NoItinerary() {
		final CargoRoutingDTOAssembler assembler = new CargoRoutingDTOAssembler();

		final Cargo cargo = new Cargo(new TrackingId("XYZ"),
				new RouteSpecification(SampleLocations.STOCKHOLM, SampleLocations.MELBOURNE, Instant.now()));
		final CargoRoutingDTO dto = assembler.toDTO(cargo);

		assertThat(dto.trackingId()).isEqualTo("XYZ");
		assertThat(dto.origin()).isEqualTo("SESTO");
		assertThat(dto.finalDestination()).isEqualTo("AUMEL");
		assertThat(dto.legs().isEmpty()).isTrue();
	}

}
