package lol.maki.cargotracker.interfaces.booking.facade.internal.assembler;

import lol.maki.cargotracker.domain.model.location.Location;
import lol.maki.cargotracker.infrastructure.sampledata.SampleLocations;
import lol.maki.cargotracker.interfaces.booking.facade.LocationDTO;
import lol.maki.cargotracker.interfaces.booking.facade.LocationDTOAssembler;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LocationDTOAssemblerTest {

	@Test
	void testToDTOList() {
		final LocationDTOAssembler assembler = new LocationDTOAssembler();
		final List<Location> locationList = List.of(SampleLocations.STOCKHOLM, SampleLocations.HAMBURG);

		final List<LocationDTO> dtos = assembler.toDTOList(locationList);

		assertThat(dtos).hasSize(2);

		LocationDTO dto = dtos.get(0);
		assertThat(dto.unLocode()).isEqualTo("SESTO");
		assertThat(dto.name()).isEqualTo("Stockholm");

		dto = dtos.get(1);
		assertThat(dto.unLocode()).isEqualTo("DEHAM");
		assertThat(dto.name()).isEqualTo("Hamburg");
	}

}
