package se.citerus.dddsample.infrastructure.persistence.jpa;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsOperations;

@TestConfiguration
public class TestConfig {
    @Bean
    public JmsOperations jmsTemplate() {
        return Mockito.mock(JmsOperations.class);
    }
}
