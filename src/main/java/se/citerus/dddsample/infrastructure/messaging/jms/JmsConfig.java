package se.citerus.dddsample.infrastructure.messaging.jms;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;

import jakarta.jms.*;

@EnableJms
@Configuration
public class JmsConfig {

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

}
