package lol.maki.cargotracker.infrastructure.persistence.jpa;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import lol.maki.cargotracker.domain.model.voyage.Voyage;
import lol.maki.cargotracker.domain.model.voyage.VoyageNumber;
import lol.maki.cargotracker.domain.model.voyage.VoyageRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestConfig.class)
class CarrierMovementRepositoryTest {

	@Autowired
	VoyageRepository voyageRepository;

	@Test
	void testFind() {
		Voyage voyage = voyageRepository.find(new VoyageNumber("0100S"));
		assertThat(voyage).isNotNull();
		assertThat(voyage.voyageNumber().idString()).isEqualTo("0100S");
		/*
		 * TODO adapt
		 * assertThat(carrierMovement.departureLocation()).isEqualTo(STOCKHOLM);
		 * assertThat(carrierMovement.arrivalLocation()).isEqualTo(HELSINKI);
		 * assertThat(carrierMovement.departureTime()).isEqualTo(DateTestUtil.toDate(
		 * "2007-09-23", "02:00"));
		 * assertThat(carrierMovement.arrivalTime()).isEqualTo(DateTestUtil.toDate(
		 * "2007-09-23", "03:00"));
		 */
	}

}
