package lol.maki.cargotracker.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import lol.maki.cargotracker.domain.model.location.Location;
import lol.maki.cargotracker.domain.model.location.LocationRepository;
import lol.maki.cargotracker.domain.model.location.UnLocode;

import java.util.List;

public interface LocationRepositoryJPA extends ListCrudRepository<Location, Long>, LocationRepository {

	@Override
	default Location find(final UnLocode unLocode) {
		return findByUnLoCode(unLocode.idString());
	}

	@Query("select loc from Location loc where loc.unlocode = :unlocode")
	Location findByUnLoCode(String unlocode);

	@Override
	default List<Location> getAll() {
		return findAll();
	}

	@Override
	default Location store(Location location) {
		return save(location);
	}

}
