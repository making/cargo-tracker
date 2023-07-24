package se.citerus.dddsample.interfaces.booking.facade;

import org.springframework.stereotype.Service;
import se.citerus.dddsample.application.BookingService;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.Itinerary;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;

import java.rmi.RemoteException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * This facade shields the domain layer - model, services, repositories - from concerns
 * about such things as the user interface.
 */
@Service
public class BookingServiceFacade {

	private final BookingService bookingService;

	private final LocationRepository locationRepository;

	private final CargoRepository cargoRepository;

	private final VoyageRepository voyageRepository;

	public BookingServiceFacade(BookingService bookingService, LocationRepository locationRepository,
			CargoRepository cargoRepository, VoyageRepository voyageRepository) {
		this.bookingService = bookingService;
		this.locationRepository = locationRepository;
		this.cargoRepository = cargoRepository;
		this.voyageRepository = voyageRepository;
	}

	public List<LocationDTO> listShippingLocations() {
		final List<Location> allLocations = locationRepository.getAll();
		final LocationDTOAssembler assembler = new LocationDTOAssembler();
		return assembler.toDTOList(allLocations);
	}

	public String bookNewCargo(String origin, String destination, Instant arrivalDeadline) {
		TrackingId trackingId = bookingService.bookNewCargo(new UnLocode(origin), new UnLocode(destination),
				arrivalDeadline);
		return trackingId.idString();
	}

	public CargoRoutingDTO loadCargoForRouting(String trackingId) {
		final Cargo cargo = cargoRepository.find(new TrackingId(trackingId));
		final CargoRoutingDTOAssembler assembler = new CargoRoutingDTOAssembler();
		return assembler.toDTO(cargo);
	}

	public void assignCargoToRoute(String trackingIdStr, RouteCandidateDTO routeCandidateDTO) {
		final Itinerary itinerary = new ItineraryCandidateDTOAssembler().fromDTO(routeCandidateDTO, voyageRepository,
				locationRepository);
		final TrackingId trackingId = new TrackingId(trackingIdStr);

		bookingService.assignCargoToRoute(itinerary, trackingId);
	}

	public void changeDestination(String trackingId, String destinationUnLocode) throws RemoteException {
		bookingService.changeDestination(new TrackingId(trackingId), new UnLocode(destinationUnLocode));
	}

	public List<CargoRoutingDTO> listAllCargos() {
		final List<Cargo> cargoList = cargoRepository.getAll();
		final List<CargoRoutingDTO> dtoList = new ArrayList<CargoRoutingDTO>(cargoList.size());
		final CargoRoutingDTOAssembler assembler = new CargoRoutingDTOAssembler();
		for (Cargo cargo : cargoList) {
			dtoList.add(assembler.toDTO(cargo));
		}
		return dtoList;
	}

	public List<RouteCandidateDTO> requestPossibleRoutesForCargo(String trackingId) throws RemoteException {
		final List<Itinerary> itineraries = bookingService.requestPossibleRoutesForCargo(new TrackingId(trackingId));

		final List<RouteCandidateDTO> routeCandidates = new ArrayList<RouteCandidateDTO>(itineraries.size());
		final ItineraryCandidateDTOAssembler dtoAssembler = new ItineraryCandidateDTOAssembler();
		for (Itinerary itinerary : itineraries) {
			routeCandidates.add(dtoAssembler.toDTO(itinerary));
		}

		return routeCandidates;
	}

}
