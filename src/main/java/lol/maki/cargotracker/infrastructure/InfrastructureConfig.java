package lol.maki.cargotracker.infrastructure;

import com.pathfinder.api.GraphTraversalService;
import com.pathfinder.internal.GraphDAO;
import com.pathfinder.internal.GraphDAOStub;
import com.pathfinder.internal.GraphTraversalServiceImpl;
import jakarta.persistence.EntityManager;
import lol.maki.cargotracker.domain.model.cargo.CargoRepository;
import lol.maki.cargotracker.infrastructure.sampledata.SampleDataGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import lol.maki.cargotracker.domain.model.handling.HandlingEventRepository;
import lol.maki.cargotracker.domain.model.location.LocationRepository;
import lol.maki.cargotracker.domain.model.voyage.VoyageRepository;

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
