package lol.maki.cargotracker.infrastructure.persistence.inmemory;

import lol.maki.cargotracker.infrastructure.sampledata.SampleVoyages;
import lol.maki.cargotracker.domain.model.voyage.Voyage;
import lol.maki.cargotracker.domain.model.voyage.VoyageNumber;
import lol.maki.cargotracker.domain.model.voyage.VoyageRepository;

public final class VoyageRepositoryInMem implements VoyageRepository {

	public Voyage find(VoyageNumber voyageNumber) {
		return SampleVoyages.lookup(voyageNumber);
	}

	@Override
	public void store(Voyage voyage) {
		// noop
	}

}
