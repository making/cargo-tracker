package lol.maki.cargotracker.infrastructure.persistence.jpa;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jms.core.JmsOperations;
import lol.maki.cargotracker.infrastructure.InfrastructureConfig;

@TestConfiguration
@Import(InfrastructureConfig.class)
public class TestConfig {

	@Bean
	public JmsOperations jmsTemplate() {
		return Mockito.mock(JmsOperations.class);
	}

}
