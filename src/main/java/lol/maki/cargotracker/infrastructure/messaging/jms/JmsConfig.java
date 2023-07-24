package lol.maki.cargotracker.infrastructure.messaging.jms;

import jakarta.jms.Queue;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;

@EnableJms
@Configuration
public class JmsConfig {

	@Bean
	public Queue cargoHandledQueue() {
		return new ActiveMQQueue(Destinations.CARGO_HANDLED_QUEUE);
	}

	@Bean
	public Queue misdirectedCargoQueue() {
		return new ActiveMQQueue(Destinations.MISDIRECTED_CARGO_QUEUE);
	}

	@Bean
	public Queue deliveredCargoQueue() {
		return new ActiveMQQueue(Destinations.DELIVERED_CARGO_QUEUE);
	}

	@Bean
	public Queue handlingEventRegistrationAttemptQueue() {
		return new ActiveMQQueue(Destinations.HANDLING_EVENT_REGISTRATION_ATTEMPT_QUEUE);
	}

	@Bean
	public Queue rejectedRegistrationAttemptsQueue() throws Exception {
		return new ActiveMQQueue(Destinations.REJECTED_REGISTRATION_ATTEMPTS_QUEUE);
	}

}
