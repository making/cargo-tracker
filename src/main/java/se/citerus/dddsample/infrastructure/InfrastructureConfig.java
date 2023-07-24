package se.citerus.dddsample.infrastructure;

import com.pathfinder.api.GraphTraversalService;
import com.pathfinder.internal.GraphDAO;
import com.pathfinder.internal.GraphDAOStub;
import com.pathfinder.internal.GraphTraversalServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;
import se.citerus.dddsample.infrastructure.sampledata.SampleDataGenerator;

@Configuration
public class InfrastructureConfig {

	@Bean
	public SampleDataGenerator sampleDataGenerator(CargoRepository cargoRepository, VoyageRepository voyageRepository,
			LocationRepository locationRepository, HandlingEventRepository handlingEventRepository,
			PlatformTransactionManager platformTransactionManager, EntityManager entityManager) {
		SampleDataGenerator sampleDataGenerator = new SampleDataGenerator(cargoRepository, voyageRepository,
				locationRepository, handlingEventRepository, platformTransactionManager);
		try {
			sampleDataGenerator.generate(); // TODO investigate if this can be called with
			// initMethod in the annotation
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return sampleDataGenerator;
	}

	private GraphDAO graphDAO() {
		return new GraphDAOStub();
	}

	@Bean
	public GraphTraversalService graphTraversalService() {
		return new GraphTraversalServiceImpl(graphDAO());
	}

}
