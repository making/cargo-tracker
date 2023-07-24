package se.citerus.dddsample.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.domain.model.cargo.*;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.service.RoutingService;

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * Cargo booking service.
 */
@Service
public class BookingService {

	private final CargoRepository cargoRepository;

	private final LocationRepository locationRepository;

	private final RoutingService routingService;

	private final CargoFactory cargoFactory;

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public BookingService(final CargoRepository cargoRepository, final LocationRepository locationRepository,
			final RoutingService routingService, final CargoFactory cargoFactory) {
		this.cargoRepository = cargoRepository;
		this.locationRepository = locationRepository;
		this.routingService = routingService;
		this.cargoFactory = cargoFactory;
	}

	/**
	 * Registers a new cargo in the tracking system, not yet routed.
	 * @param originUnLocode cargo origin
	 * @param destinationUnLocode cargo destination
	 * @param arrivalDeadline arrival deadline
	 * @return Cargo tracking id
	 */
	@Transactional
	public TrackingId bookNewCargo(final UnLocode originUnLocode, final UnLocode destinationUnLocode,
			final Instant arrivalDeadline) {
		Cargo cargo = cargoFactory.createCargo(originUnLocode, destinationUnLocode, arrivalDeadline);

		cargoRepository.store(cargo);
		logger.info("Booked new cargo with tracking id {}", cargo.trackingId().idString());

		return cargo.trackingId();
	}

	/**
	 * Requests a list of itineraries describing possible routes for this cargo.
	 * @param trackingId cargo tracking id
	 * @return A list of possible itineraries for this cargo
	 */
	@Transactional
	public List<Itinerary> requestPossibleRoutesForCargo(final TrackingId trackingId) {
		final Cargo cargo = cargoRepository.find(trackingId);

		if (cargo == null) {
			return Collections.emptyList();
		}

		return routingService.fetchRoutesForSpecification(cargo.routeSpecification());
	}

	/**
	 * @param itinerary itinerary describing the selected route
	 * @param trackingId cargo tracking id
	 */
	@Transactional
	public void assignCargoToRoute(final Itinerary itinerary, final TrackingId trackingId) {
		final Cargo cargo = cargoRepository.find(trackingId);
		if (cargo == null) {
			throw new IllegalArgumentException("Can't assign itinerary to non-existing cargo " + trackingId);
		}

		cargo.assignToRoute(itinerary);
		cargoRepository.store(cargo);

		logger.info("Assigned cargo {} to new route", trackingId);
	}

	/**
	 * Changes the destination of a cargo.
	 * @param trackingId cargo tracking id
	 * @param unLocode UN locode of new destination
	 */
	@Transactional
	public void changeDestination(final TrackingId trackingId, final UnLocode unLocode) {
		final Cargo cargo = cargoRepository.find(trackingId);
		final Location newDestination = locationRepository.find(unLocode);

		final RouteSpecification routeSpecification = new RouteSpecification(cargo.origin(), newDestination,
				cargo.routeSpecification().arrivalDeadline());
		cargo.specifyNewRoute(routeSpecification);

		cargoRepository.store(cargo);
		logger.info("Changed destination for cargo {} to {}", trackingId, routeSpecification.destination());
	}

}
