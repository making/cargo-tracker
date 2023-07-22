package se.citerus.dddsample.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;

import java.util.List;

/**
 * Hibernate implementation of CargoRepository.
 */
public interface CargoRepositoryJPA extends ListCrudRepository<Cargo, Long>, CargoRepository {

	@Override
	default Cargo find(TrackingId trackingId) {
		return findByTrackingId(trackingId.idString());
	}

	@Query("select c from Cargo c where c.trackingId = :trackingId")
	Cargo findByTrackingId(String trackingId);

	@Override
	default void store(final Cargo cargo) {
		save(cargo);
	}

	@Override
	default List<Cargo> getAll() {
		return findAll();
	}

	@Query(value = "SELECT UPPER(SUBSTR(CAST(UUID() AS VARCHAR(38)), 0, 9)) AS id FROM (VALUES(0))", nativeQuery = true)
	String nextTrackingIdString();

	@Override
	default TrackingId nextTrackingId() {
		return new TrackingId(nextTrackingIdString());
	}

}
