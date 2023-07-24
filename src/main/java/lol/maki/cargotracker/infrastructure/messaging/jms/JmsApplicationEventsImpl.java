package lol.maki.cargotracker.infrastructure.messaging.jms;

import lol.maki.cargotracker.application.ApplicationEvents;
import lol.maki.cargotracker.application.HandlingEventRegistrationAttempt;
import lol.maki.cargotracker.domain.model.cargo.Cargo;
import lol.maki.cargotracker.domain.model.handling.HandlingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsOperations;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

/**
 * JMS based implementation.
 */
@Component
public final class JmsApplicationEventsImpl implements ApplicationEvents {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final JmsOperations jmsOperations;

	public JmsApplicationEventsImpl(JmsOperations jmsOperations) {
		this.jmsOperations = jmsOperations;
	}

	@Override
	public void cargoWasHandled(HandlingEvent event) {
		final Cargo cargo = event.cargo();
		logger.info("Cargo was handled {}", cargo);
		jmsOperations.convertAndSend(Destinations.CARGO_HANDLED_QUEUE, cargo.trackingId().idString());
	}

	@Override
	public void cargoWasMisdirected(Cargo cargo) {
		logger.info("Cargo was misdirected {}", cargo);
		jmsOperations.convertAndSend(Destinations.MISDIRECTED_CARGO_QUEUE, cargo.trackingId().idString());
	}

	@Override
	public void cargoHasArrived(Cargo cargo) {
		logger.info("Cargo has arrived {}", cargo);
		jmsOperations.convertAndSend(Destinations.DELIVERED_CARGO_QUEUE, cargo.trackingId().idString());
	}

	@Override
	public void receivedHandlingEventRegistrationAttempt(HandlingEventRegistrationAttempt attempt) {
		logger.info("Received handling event registration attempt {}", attempt);
		jmsOperations.convertAndSend(Destinations.HANDLING_EVENT_REGISTRATION_ATTEMPT_QUEUE, attempt);
	}

}
