package se.citerus.dddsample.application;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.handling.HandlingHistory;

import java.lang.invoke.MethodHandles;

/**
 * Cargo inspection service.
 */
@Service
public class CargoInspectionService {

	private final ApplicationEvents applicationEvents;

	private final CargoRepository cargoRepository;

	private final HandlingEventRepository handlingEventRepository;

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public CargoInspectionService(final ApplicationEvents applicationEvents, final CargoRepository cargoRepository,
			final HandlingEventRepository handlingEventRepository) {
		this.applicationEvents = applicationEvents;
		this.cargoRepository = cargoRepository;
		this.handlingEventRepository = handlingEventRepository;
	}

	/**
	 * Inspect cargo and send relevant notifications to interested parties, for example if
	 * a cargo has been misdirected, or unloaded at the final destination.
	 * @param trackingId cargo tracking id
	 */
	@Transactional
	public void inspectCargo(final TrackingId trackingId) {
		Validate.notNull(trackingId, "Tracking ID is required");

		final Cargo cargo = cargoRepository.find(trackingId);
		if (cargo == null) {
			logger.warn("Can't inspect non-existing cargo {}", trackingId);
			return;
		}

		final HandlingHistory handlingHistory = handlingEventRepository.lookupHandlingHistoryOfCargo(trackingId);

		cargo.deriveDeliveryProgress(handlingHistory);

		if (cargo.delivery().isMisdirected()) {
			applicationEvents.cargoWasMisdirected(cargo);
		}

		if (cargo.delivery().isUnloadedAtDestination()) {
			applicationEvents.cargoHasArrived(cargo);
		}

		cargoRepository.store(cargo);
	}

}
