package lol.maki.cargotracker.infrastructure.persistence.jpa;

import lol.maki.cargotracker.domain.model.cargo.TrackingId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import lol.maki.cargotracker.domain.model.handling.HandlingEvent;
import lol.maki.cargotracker.domain.model.handling.HandlingEventRepository;
import lol.maki.cargotracker.domain.model.handling.HandlingHistory;

import java.util.List;

/**
 * Hibernate implementation of HandlingEventRepository.
 */
public interface HandlingEventRepositoryJPA extends ListCrudRepository<HandlingEvent, Long>, HandlingEventRepository {

	@Override
	default void store(final HandlingEvent event) {
		save(event);
	}

	@Override
	default HandlingHistory lookupHandlingHistoryOfCargo(final TrackingId trackingId) {
		return new HandlingHistory(getHandlingHistoryOfCargo(trackingId.idString()));
	}

	@Query("select he from HandlingEvent he where he.cargo.trackingId = :trackingId and he.location != NULL")
	List<HandlingEvent> getHandlingHistoryOfCargo(String trackingId);

}
