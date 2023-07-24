package lol.maki.cargotracker.interfaces.booking.facade;

import lol.maki.cargotracker.domain.model.location.Location;

import java.util.ArrayList;
import java.util.List;

public class LocationDTOAssembler {

	public LocationDTO toDTO(Location location) {
		return new LocationDTO(location.unLocode().idString(), location.name());
	}

	public List<LocationDTO> toDTOList(List<Location> allLocations) {
		final List<LocationDTO> dtoList = new ArrayList<LocationDTO>(allLocations.size());
		for (Location location : allLocations) {
			dtoList.add(toDTO(location));
		}
		return dtoList;
	}

}
