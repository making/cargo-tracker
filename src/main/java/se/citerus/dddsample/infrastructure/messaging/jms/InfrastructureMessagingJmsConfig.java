package se.citerus.dddsample.infrastructure.messaging.jms;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsOperations;
import se.citerus.dddsample.application.ApplicationEvents;

import jakarta.jms.*;

@EnableJms
@Configuration
public class InfrastructureMessagingJmsConfig {

	@Bean
	public Queue cargoHandledQueue() {
		return new ActiveMQQueue("CargoHandledQueue");
	}

	@Bean
	public Queue misdirectedCargoQueue() {
		return new ActiveMQQueue("MisdirectedCargoQueue");
	}

	@Bean
	public Queue deliveredCargoQueue() {
		return new ActiveMQQueue("DeliveredCargoQueue");
	}

	@Bean
	public Queue handlingEventRegistrationAttemptQueue() {
		return new ActiveMQQueue("HandlingEventRegistrationAttemptQueue");
	}

	@Bean
	public Queue rejectedRegistrationAttemptsQueue() throws Exception {
		return new ActiveMQQueue("RejectedRegistrationAttemptsQueue");
	}

	@Bean
	public ApplicationEvents applicationEvents(JmsOperations jmsOperations,
			@Qualifier("cargoHandledQueue") Destination cargoHandledQueue,
			@Qualifier("misdirectedCargoQueue") Destination misdirectedCargoQueue,
			@Qualifier("deliveredCargoQueue") Destination deliveredCargoQueue,
			@Qualifier("rejectedRegistrationAttemptsQueue") Destination rejectedRegistrationAttemptsQueue,
			@Qualifier("handlingEventRegistrationAttemptQueue") Destination handlingEventRegistrationAttemptQueue) {
		return new JmsApplicationEventsImpl(jmsOperations, cargoHandledQueue, misdirectedCargoQueue,
				deliveredCargoQueue, rejectedRegistrationAttemptsQueue, handlingEventRegistrationAttemptQueue);
	}

}
