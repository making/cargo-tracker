package lol.maki.cargotracker.infrastructure.persistence.jpa;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import lol.maki.cargotracker.domain.model.location.Location;
import lol.maki.cargotracker.domain.model.location.LocationRepository;
import lol.maki.cargotracker.domain.model.location.UnLocode;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestConfig.class)
class LocationRepositoryTest {

	@Autowired
	private LocationRepository locationRepository;

	@Test
	void testFind() {
		final UnLocode melbourne = new UnLocode("AUMEL");
		Location location = locationRepository.find(melbourne);
		assertThat(location).isNotNull();
		assertThat(location.unLocode()).isEqualTo(melbourne);

		assertThat(locationRepository.find(new UnLocode("NOLOC"))).isNull();
	}

	@Test
	void testFindAll() {
		List<Location> allLocations = locationRepository.getAll();

		assertThat(allLocations).isNotNull().hasSize(13);
	}

}
