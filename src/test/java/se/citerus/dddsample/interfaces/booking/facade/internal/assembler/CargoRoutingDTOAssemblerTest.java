package se.citerus.dddsample.interfaces.booking.facade.internal.assembler;

import org.junit.jupiter.api.Test;
import se.citerus.dddsample.domain.model.cargo.*;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.interfaces.booking.facade.CargoRoutingDTO;
import se.citerus.dddsample.interfaces.booking.facade.CargoRoutingDTOAssembler;
import se.citerus.dddsample.interfaces.booking.facade.LegDTO;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static se.citerus.dddsample.infrastructure.sampledata.SampleLocations.*;
import static se.citerus.dddsample.infrastructure.sampledata.SampleVoyages.CM001;

class CargoRoutingDTOAssemblerTest {

	@Test
	void testToDTO() {
		final CargoRoutingDTOAssembler assembler = new CargoRoutingDTOAssembler();

		final Location origin = STOCKHOLM;
		final Location destination = MELBOURNE;
		final Cargo cargo = new Cargo(new TrackingId("XYZ"),
				new RouteSpecification(origin, destination, Instant.now()));

		final Itinerary itinerary = new Itinerary(
				List.of(new Leg(CM001, origin, SHANGHAI, Instant.now(), Instant.now()),
						new Leg(CM001, ROTTERDAM, destination, Instant.now(), Instant.now())));

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
				new RouteSpecification(STOCKHOLM, MELBOURNE, Instant.now()));
		final CargoRoutingDTO dto = assembler.toDTO(cargo);

		assertThat(dto.trackingId()).isEqualTo("XYZ");
		assertThat(dto.origin()).isEqualTo("SESTO");
		assertThat(dto.finalDestination()).isEqualTo("AUMEL");
		assertThat(dto.legs().isEmpty()).isTrue();
	}

}
