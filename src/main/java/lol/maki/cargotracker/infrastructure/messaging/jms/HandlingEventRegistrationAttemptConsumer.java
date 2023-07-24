package lol.maki.cargotracker.infrastructure.messaging.jms;

import lol.maki.cargotracker.application.HandlingEventRegistrationAttempt;
import lol.maki.cargotracker.application.HandlingEventService;
import lol.maki.cargotracker.domain.model.handling.CannotCreateHandlingEventException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

/**
 * Consumes handling event registration attempt messages and delegates to proper
 * registration.
 */
@Component
public class HandlingEventRegistrationAttemptConsumer {

	private final HandlingEventService handlingEventService;

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public HandlingEventRegistrationAttemptConsumer(HandlingEventService handlingEventService) {
		this.handlingEventService = handlingEventService;
	}

	@JmsListener(destination = Destinations.HANDLING_EVENT_REGISTRATION_ATTEMPT_QUEUE)
	public void onMessage(HandlingEventRegistrationAttempt attempt) {
		logger.info("HandlingEventRegistrationAttemptQueue#onMessage({})", attempt);
		try {
			handlingEventService.registerHandlingEvent(attempt.getCompletionTime(), attempt.getTrackingId(),
					attempt.getVoyageNumber(), attempt.getUnLocode(), attempt.getType());
		}
		catch (CannotCreateHandlingEventException e) {
			logger.error("Error consuming HandlingEventRegistrationAttempt message", e);
		}
	}

}
