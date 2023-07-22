package se.citerus.dddsample.infrastructure.messaging.jms;

import jakarta.jms.Destination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsOperations;
import org.springframework.stereotype.Component;
import se.citerus.dddsample.application.ApplicationEvents;
import se.citerus.dddsample.application.HandlingEventRegistrationAttempt;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;

import java.lang.invoke.MethodHandles;

/**
 * JMS based implementation.
 */
@Component
public final class JmsApplicationEventsImpl implements ApplicationEvents {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final JmsOperations jmsOperations;

	private final Destination cargoHandledQueue;

	private final Destination misdirectedCargoQueue;

	private final Destination deliveredCargoQueue;

	private final Destination rejectedRegistrationAttemptsQueue; // TODO why is this

	// unused?

	private final Destination handlingEventQueue;

	public JmsApplicationEventsImpl(JmsOperations jmsOperations,
			@Qualifier("cargoHandledQueue") Destination cargoHandledQueue,
			@Qualifier("misdirectedCargoQueue") Destination misdirectedCargoQueue,
			@Qualifier("deliveredCargoQueue") Destination deliveredCargoQueue,
			@Qualifier("rejectedRegistrationAttemptsQueue") Destination rejectedRegistrationAttemptsQueue,
			@Qualifier("handlingEventRegistrationAttemptQueue") Destination handlingEventQueue) {
		this.jmsOperations = jmsOperations;
		this.cargoHandledQueue = cargoHandledQueue;
		this.misdirectedCargoQueue = misdirectedCargoQueue;
		this.deliveredCargoQueue = deliveredCargoQueue;
		this.rejectedRegistrationAttemptsQueue = rejectedRegistrationAttemptsQueue;
		this.handlingEventQueue = handlingEventQueue;
	}

	@Override
	public void cargoWasHandled(final HandlingEvent event) {
		final Cargo cargo = event.cargo();
		logger.info("Cargo was handled {}", cargo);
		jmsOperations.send(cargoHandledQueue, session -> session.createTextMessage(cargo.trackingId().idString()));
	}

	@Override
	public void cargoWasMisdirected(final Cargo cargo) {
		logger.info("Cargo was misdirected {}", cargo);
		jmsOperations.send(misdirectedCargoQueue, session -> session.createTextMessage(cargo.trackingId().idString()));
	}

	@Override
	public void cargoHasArrived(final Cargo cargo) {
		logger.info("Cargo has arrived {}", cargo);
		jmsOperations.send(deliveredCargoQueue, session -> session.createTextMessage(cargo.trackingId().idString()));
	}

	@Override
	public void receivedHandlingEventRegistrationAttempt(final HandlingEventRegistrationAttempt attempt) {
		logger.info("Received handling event registration attempt {}", attempt);
		jmsOperations.send(handlingEventQueue, session -> session.createObjectMessage(attempt));
	}

}
