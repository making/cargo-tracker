package se.citerus.dddsample.infrastructure.messaging.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.jms.Message;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
public class SimpleLoggingConsumer {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @JmsListener(destination = "misdirectedCargoQueue")
    @JmsListener(destination = "deliveredCargoConsumer")
    @JmsListener(destination = "rejectedRegistrationAttemptsConsumer")
    public void onMessage(Message message) {
        logger.debug("Received JMS message: {}", message);
    }

}
