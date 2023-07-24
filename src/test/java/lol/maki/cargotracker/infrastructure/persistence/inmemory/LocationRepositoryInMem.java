package lol.maki.cargotracker.infrastructure.persistence.inmemory;

import lol.maki.cargotracker.infrastructure.sampledata.SampleLocations;
import lol.maki.cargotracker.domain.model.location.Location;
import lol.maki.cargotracker.domain.model.location.LocationRepository;
import lol.maki.cargotracker.domain.model.location.UnLocode;

import java.util.List;

public class LocationRepositoryInMem implements LocationRepository {

	public Location find(UnLocode unLocode) {
		for (Location location : SampleLocations.getAll()) {
			if (location.unLocode().equals(unLocode)) {
				return location;
			}
		}
		return null;
	}

	public List<Location> getAll() {
		return SampleLocations.getAll();
	}

	@Override
	public Location store(Location location) {
		return location;
	}

}
