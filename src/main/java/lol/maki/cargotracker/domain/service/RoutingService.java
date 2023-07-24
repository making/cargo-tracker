package lol.maki.cargotracker.domain.service;

import lol.maki.cargotracker.domain.model.cargo.Itinerary;
import lol.maki.cargotracker.domain.model.cargo.RouteSpecification;

import java.util.List;

/**
 * Routing service.
 */
public interface RoutingService {

	/**
	 * @param routeSpecification route specification
	 * @return A list of itineraries that satisfy the specification. May be an empty list
	 * if no route is found.
	 */
	List<Itinerary> fetchRoutesForSpecification(RouteSpecification routeSpecification);

}
