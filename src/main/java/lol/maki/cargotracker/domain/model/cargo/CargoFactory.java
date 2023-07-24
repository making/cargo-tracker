package lol.maki.cargotracker.domain.model.cargo;

import org.springframework.stereotype.Component;
import lol.maki.cargotracker.domain.model.location.Location;
import lol.maki.cargotracker.domain.model.location.LocationRepository;
import lol.maki.cargotracker.domain.model.location.UnLocode;

import java.time.Instant;

@Component
public class CargoFactory {

	private final LocationRepository locationRepository;

	private final CargoRepository cargoRepository;

	public CargoFactory(LocationRepository locationRepository, CargoRepository cargoRepository) {
		this.locationRepository = locationRepository;
		this.cargoRepository = cargoRepository;
	}

	public Cargo createCargo(UnLocode originUnLoCode, UnLocode destinationUnLoCode, Instant arrivalDeadline) {
		final TrackingId trackingId = cargoRepository.nextTrackingId();
		final Location origin = locationRepository.find(originUnLoCode);
		final Location destination = locationRepository.find(destinationUnLoCode);
		final RouteSpecification routeSpecification = new RouteSpecification(origin, destination, arrivalDeadline);

		return new Cargo(trackingId, routeSpecification);
	}

}
