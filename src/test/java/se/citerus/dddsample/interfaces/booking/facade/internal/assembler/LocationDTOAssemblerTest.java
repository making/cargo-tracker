package se.citerus.dddsample.interfaces.booking.facade.internal.assembler;

import org.junit.jupiter.api.Test;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.interfaces.booking.facade.dto.LocationDTO;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static se.citerus.dddsample.infrastructure.sampledata.SampleLocations.HAMBURG;
import static se.citerus.dddsample.infrastructure.sampledata.SampleLocations.STOCKHOLM;

class LocationDTOAssemblerTest {

	@Test
	void testToDTOList() {
		final LocationDTOAssembler assembler = new LocationDTOAssembler();
		final List<Location> locationList = List.of(STOCKHOLM, HAMBURG);

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
