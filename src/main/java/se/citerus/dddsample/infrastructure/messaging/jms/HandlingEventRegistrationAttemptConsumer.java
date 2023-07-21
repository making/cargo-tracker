package se.citerus.dddsample.infrastructure.messaging.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import se.citerus.dddsample.application.HandlingEventService;
import se.citerus.dddsample.interfaces.handling.HandlingEventRegistrationAttempt;

import jakarta.jms.Message;
import jakarta.jms.ObjectMessage;

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

	@JmsListener(destination = "handlingEventRegistrationAttemptQueue")
	public void onMessage(final Message message) {
		try {
			final ObjectMessage om = (ObjectMessage) message;
			HandlingEventRegistrationAttempt attempt = (HandlingEventRegistrationAttempt) om.getObject();
			handlingEventService.registerHandlingEvent(attempt.getCompletionTime(), attempt.getTrackingId(),
					attempt.getVoyageNumber(), attempt.getUnLocode(), attempt.getType());
		}
		catch (Exception e) {
			logger.error("Error consuming HandlingEventRegistrationAttempt message", e);
		}
	}

}
