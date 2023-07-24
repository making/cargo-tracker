package lol.maki.cargotracker.interfaces.tracking.web;

import lol.maki.cargotracker.domain.model.cargo.Cargo;
import lol.maki.cargotracker.domain.model.cargo.CargoRepository;
import lol.maki.cargotracker.domain.model.cargo.TrackingId;
import lol.maki.cargotracker.domain.model.handling.HandlingEvent;
import lol.maki.cargotracker.domain.model.handling.HandlingEventRepository;
import lol.maki.cargotracker.interfaces.booking.web.CargoAdminController;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Locale;

/**
 * Controller for tracking cargo. This interface sits immediately on top of the domain
 * layer, unlike the booking interface which has a a remote facade and supporting DTOs in
 * between.
 * <p>
 * An adapter class, designed for the tracking use case, is used to wrap the domain model
 * to make it easier to work with in a web page rendering context. We do not want to apply
 * view rendering constraints to the design of our domain model, and the adapter helps us
 * shield the domain model classes.
 * <p>
 *
 * @eee se.citerus.dddsample.application.web.CargoTrackingViewAdapter
 * @see CargoAdminController
 */
@Controller
@RequestMapping("/track")
public final class CargoTrackingController {

	private final CargoRepository cargoRepository;

	private final HandlingEventRepository handlingEventRepository;

	private final MessageSource messageSource;

	private final TrackCommandValidator trackCommandValidator;

	public CargoTrackingController(CargoRepository cargoRepository, HandlingEventRepository handlingEventRepository,
			MessageSource messageSource, TrackCommandValidator trackCommandValidator) {
		this.cargoRepository = cargoRepository;
		this.handlingEventRepository = handlingEventRepository;
		this.messageSource = messageSource;
		this.trackCommandValidator = trackCommandValidator;
	}

	@GetMapping
	public String get(Model model) {
		model.addAttribute("trackCommand", new TrackCommand()); // TODO why is this method
																// adding a
		// TrackCommand without id?
		return "track";
	}

	@PostMapping
	private String onSubmit(TrackCommand command, BindingResult bindingResult, Model model, Locale locale) {
		this.trackCommandValidator.validate(command, bindingResult);
		final TrackingId trackingId = new TrackingId(command.getTrackingId());
		final Cargo cargo = cargoRepository.find(trackingId);

		if (cargo != null) {
			final List<HandlingEvent> handlingEvents = handlingEventRepository.lookupHandlingHistoryOfCargo(trackingId)
				.distinctEventsByCompletionTime();
			model.addAttribute("cargo", new CargoTrackingViewAdapter(cargo, messageSource, locale, handlingEvents));
		}
		else {
			bindingResult.rejectValue("trackingId", "cargo.unknown_id", new Object[] { command.getTrackingId() },
					"Unknown tracking id");
		}
		return "track";
	}

}
