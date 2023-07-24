package lol.maki.cargotracker.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import lol.maki.cargotracker.domain.model.voyage.Voyage;
import lol.maki.cargotracker.domain.model.voyage.VoyageNumber;
import lol.maki.cargotracker.domain.model.voyage.VoyageRepository;

/**
 * Hibernate implementation of CarrierMovementRepository.
 */
public interface VoyageRepositoryJPA extends ListCrudRepository<Voyage, Long>, VoyageRepository {

	@Override
	default Voyage find(final VoyageNumber voyageNumber) {
		return findByVoyageNumber(voyageNumber.idString());
	}

	@Query("select v from Voyage v where v.voyageNumber = :voyageNumber")
	Voyage findByVoyageNumber(String voyageNumber);

	@Override
	default void store(Voyage voyage) {
		save(voyage);
	}

}
