package se.citerus.dddsample.infrastructure.messaging.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import se.citerus.dddsample.application.HandlingEventRegistrationAttempt;
import se.citerus.dddsample.application.HandlingEventService;
import se.citerus.dddsample.domain.model.handling.CannotCreateHandlingEventException;

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
