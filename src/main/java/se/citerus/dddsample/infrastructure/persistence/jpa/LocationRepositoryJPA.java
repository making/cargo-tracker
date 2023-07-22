package se.citerus.dddsample.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListCrudRepository;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
