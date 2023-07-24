package se.citerus.dddsample.infrastructure.messaging.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
public class SimpleLoggingConsumer {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@JmsListener(destination = Destinations.MISDIRECTED_CARGO_QUEUE)
	@JmsListener(destination = Destinations.DELIVERED_CARGO_QUEUE)
	@JmsListener(destination = Destinations.REJECTED_REGISTRATION_ATTEMPTS_QUEUE)
	public void onMessage(Message<?> message) {
		logger.info("Received message: {} {}", message.getPayload(), message.getHeaders());
	}

}
