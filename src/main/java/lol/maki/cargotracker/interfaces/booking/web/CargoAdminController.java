package lol.maki.cargotracker.interfaces.booking.web;

import lol.maki.cargotracker.interfaces.booking.facade.*;
import lol.maki.cargotracker.interfaces.tracking.web.CargoTrackingController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles cargo booking and routing. Operates against a dedicated remoting service
 * facade, and could easily be rewritten as a thick Swing client. Completely separated
 * from the domain layer, unlike the tracking user interface.
 * <p>
 * In order to successfully keep the domain model shielded from user interface
 * considerations, this approach is generally preferred to the one taken in the tracking
 * controller. However, there is never any one perfect solution for all situations, so
 * we've chosen to demonstrate two polarized ways to build user interfaces.
 *
 * @see CargoTrackingController
 */
@Controller
@RequestMapping("/admin")
public final class CargoAdminController {

	private final BookingServiceFacade bookingServiceFacade;

	public CargoAdminController(BookingServiceFacade bookingServiceFacade) {
		this.bookingServiceFacade = bookingServiceFacade;
	}

	@GetMapping("/registration")
	public String registration(Model model) throws Exception {
		List<LocationDTO> dtoList = bookingServiceFacade.listShippingLocations();
		List<String> unLocodeStrings = dtoList.stream().map(LocationDTO::unLocode).collect(Collectors.toList());

		model.addAttribute("unlocodes", unLocodeStrings);
		model.addAttribute("locations", dtoList);
		return "admin/registrationForm";
	}

	@PostMapping("/register")
	public String register(RegistrationCommand command, RedirectAttributes attributes) throws Exception {
		LocalDate arrivalDeadline = LocalDate.parse(command.getArrivalDeadline(),
				DateTimeFormatter.ofPattern("dd/MM/uuuu"));
		String trackingId = bookingServiceFacade.bookNewCargo(command.getOriginUnlocode(),
				command.getDestinationUnlocode(), arrivalDeadline.atStartOfDay().toInstant(ZoneOffset.UTC));

		attributes.addAttribute("trackingId", trackingId);
		return "redirect:show";
	}

	@GetMapping("/list")
	public String list(Model model) throws Exception {
		List<CargoRoutingDTO> cargoList = bookingServiceFacade.listAllCargos();

		model.addAttribute("cargoList", cargoList);
		return "admin/list";
	}

	@GetMapping("/show")
	public String show(@RequestParam String trackingId, Model model) throws Exception {
		CargoRoutingDTO dto = bookingServiceFacade.loadCargoForRouting(trackingId);

		model.addAttribute("cargo", dto);
		return "admin/show";
	}

	@GetMapping("/selectItinerary")
	public String selectItinerary(@RequestParam String trackingId, Model model) throws Exception {
		List<RouteCandidateDTO> routeCandidates = bookingServiceFacade.requestPossibleRoutesForCargo(trackingId);
		CargoRoutingDTO cargoDTO = bookingServiceFacade.loadCargoForRouting(trackingId);

		model.addAttribute("routeCandidates", routeCandidates);
		model.addAttribute("cargo", cargoDTO);
		return "admin/selectItinerary";
	}

	@PostMapping("/assignItinerary")
	public String assignItinerary(RouteAssignmentCommand command, RedirectAttributes attributes) throws Exception {
		List<LegDTO> legDTOs = command.getLegs()
			.stream()
			.map(leg -> new LegDTO(leg.getVoyageNumber(), leg.getFromUnLocode(), leg.getToUnLocode(),
					leg.getFromDate().toInstant(ZoneOffset.UTC), leg.getToDate().toInstant(ZoneOffset.UTC)))
			.collect(Collectors.toCollection(() -> new ArrayList<>(command.getLegs().size())));
		RouteCandidateDTO selectedRoute = new RouteCandidateDTO(legDTOs);
		bookingServiceFacade.assignCargoToRoute(command.getTrackingId(), selectedRoute);

		attributes.addAttribute("trackingId", command.getTrackingId());
		return "redirect:show";
	}

	@GetMapping("/pickNewDestination")
	public String pickNewDestination(@RequestParam String trackingId, Model model) throws Exception {
		List<LocationDTO> locations = bookingServiceFacade.listShippingLocations();
		CargoRoutingDTO cargo = bookingServiceFacade.loadCargoForRouting(trackingId);

		model.addAttribute("locations", locations);
		model.addAttribute("cargo", cargo);
		return "admin/pickNewDestination";
	}

	@PostMapping("/changeDestination")
	public String changeDestination(@RequestParam String trackingId, @RequestParam String unlocode,
			RedirectAttributes attributes) throws Exception {
		bookingServiceFacade.changeDestination(trackingId, unlocode);

		attributes.addAttribute("trackingId", trackingId);
		return "redirect:show";
	}

}
