package lol.maki.cargotracker.interfaces.tracking.api;

import lol.maki.cargotracker.domain.model.cargo.CargoRepository;
import lol.maki.cargotracker.domain.model.cargo.TrackingId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import lol.maki.cargotracker.domain.model.cargo.Cargo;
import lol.maki.cargotracker.domain.model.handling.HandlingEvent;
import lol.maki.cargotracker.domain.model.handling.HandlingEventRepository;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Locale;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class CargoTrackingRestController {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final CargoRepository cargoRepository;

	private final HandlingEventRepository handlingEventRepository;

	private final MessageSource messageSource;

	public CargoTrackingRestController(CargoRepository cargoRepository, HandlingEventRepository handlingEventRepository,
			MessageSource messageSource) {
		this.cargoRepository = cargoRepository;
		this.handlingEventRepository = handlingEventRepository;
		this.messageSource = messageSource;
	}

	@GetMapping(value = "/api/track/{trackingId}", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<CargoTrackingDTO> trackCargo(@PathVariable TrackingId trackingId, Locale locale) {
		try {
			Cargo cargo = cargoRepository.find(trackingId);
			if (cargo == null) {
				return ResponseEntity.notFound().build();
			}
			final List<HandlingEvent> handlingEvents = handlingEventRepository.lookupHandlingHistoryOfCargo(trackingId)
				.distinctEventsByCompletionTime();
			return ResponseEntity.ok(CargoTrackingDTOConverter.convert(cargo, handlingEvents, messageSource, locale));
		}
		catch (Exception e) {
			log.error("Unexpected error in trackCargo endpoint", e);
			return ResponseEntity.internalServerError().build();
		}
	}

}
