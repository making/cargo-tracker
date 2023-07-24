package lol.maki.cargotracker.interfaces.booking.facade.internal.assembler;

import lol.maki.cargotracker.domain.model.cargo.Itinerary;
import lol.maki.cargotracker.domain.model.cargo.Leg;
import lol.maki.cargotracker.domain.model.location.Location;
import lol.maki.cargotracker.domain.model.location.LocationRepository;
import lol.maki.cargotracker.domain.model.location.UnLocode;
import lol.maki.cargotracker.infrastructure.persistence.inmemory.VoyageRepositoryInMem;
import lol.maki.cargotracker.infrastructure.sampledata.SampleLocations;
import lol.maki.cargotracker.infrastructure.sampledata.SampleVoyages;
import lol.maki.cargotracker.interfaces.booking.facade.ItineraryCandidateDTOAssembler;
import lol.maki.cargotracker.interfaces.booking.facade.LegDTO;
import lol.maki.cargotracker.interfaces.booking.facade.RouteCandidateDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import lol.maki.cargotracker.domain.model.voyage.VoyageRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItineraryCandidateDTOAssemblerTest {

	@Test
	void testToDTO() {
		final ItineraryCandidateDTOAssembler assembler = new ItineraryCandidateDTOAssembler();

		final Location origin = SampleLocations.STOCKHOLM;
		final Location destination = SampleLocations.MELBOURNE;

		final Itinerary itinerary = new Itinerary(List.of(
				new Leg(SampleVoyages.CM001, origin, SampleLocations.SHANGHAI, Instant.now(), Instant.now()),
				new Leg(SampleVoyages.CM001, SampleLocations.ROTTERDAM, destination, Instant.now(), Instant.now())));

		final RouteCandidateDTO dto = assembler.toDTO(itinerary);

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
	void testFromDTO() {
		final ItineraryCandidateDTOAssembler assembler = new ItineraryCandidateDTOAssembler();

		final List<LegDTO> legs = new ArrayList<LegDTO>();
		legs.add(new LegDTO("CM001", "AAAAA", "BBBBB", Instant.now(), Instant.now()));
		legs.add(new LegDTO("CM001", "BBBBB", "CCCCC", Instant.now(), Instant.now()));

		final LocationRepository locationRepository = mock(LocationRepository.class);
		when(locationRepository.find(new UnLocode("AAAAA"))).thenReturn(SampleLocations.HONGKONG);
		when(locationRepository.find(new UnLocode("BBBBB"))).thenReturn(SampleLocations.TOKYO);
		when(locationRepository.find(new UnLocode("CCCCC"))).thenReturn(SampleLocations.CHICAGO);

		final VoyageRepository voyageRepository = new VoyageRepositoryInMem();

		// Tested call
		final Itinerary itinerary = assembler.fromDTO(new RouteCandidateDTO(legs), voyageRepository,
				locationRepository);

		assertThat(itinerary).isNotNull();
		assertThat(itinerary.legs()).isNotNull();
		assertThat(itinerary.legs()).hasSize(2);

		final Leg leg1 = itinerary.legs().get(0);
		assertThat(leg1).isNotNull();
		Assertions.assertThat(leg1.loadLocation()).isEqualTo(SampleLocations.HONGKONG);
		Assertions.assertThat(leg1.unloadLocation()).isEqualTo(SampleLocations.TOKYO);

		final Leg leg2 = itinerary.legs().get(1);
		assertThat(leg2).isNotNull();
		Assertions.assertThat(leg2.loadLocation()).isEqualTo(SampleLocations.TOKYO);
		Assertions.assertThat(leg2.unloadLocation()).isEqualTo(SampleLocations.CHICAGO);
	}

}
