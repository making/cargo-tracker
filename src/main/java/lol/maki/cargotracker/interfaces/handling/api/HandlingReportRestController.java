package lol.maki.cargotracker.interfaces.handling.api;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import lol.maki.cargotracker.application.ApplicationEvents;
import lol.maki.cargotracker.application.HandlingEventRegistrationAttempt;
import lol.maki.cargotracker.interfaces.handling.HandlingReport;

import java.lang.invoke.MethodHandles;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static lol.maki.cargotracker.interfaces.handling.HandlingReportParser.parse;

/**
 * This web service endpoint implementation performs basic validation and parsing of
 * incoming data, and in case of a valid registration attempt, sends an asynchronous
 * message with the information to the handling event registration system for proper
 * registration.
 */
@RestController
public class HandlingReportRestController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final ApplicationEvents applicationEvents;

	public HandlingReportRestController(ApplicationEvents applicationEvents) {
		this.applicationEvents = applicationEvents;
	}

	@PostMapping(value = "/handlingReport", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<?> submitReport(@Valid @RequestBody HandlingReport handlingReport) {
		try {
			List<HandlingEventRegistrationAttempt> attempts = parse(handlingReport);
			attempts.forEach(applicationEvents::receivedHandlingEventRegistrationAttempt);
		}
		catch (Exception e) {
			logger.error("Unexpected error in submitReport", e);
			return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Internal server error: " + e.getMessage());
		}
		return ResponseEntity.status(CREATED).build();
	}

}
