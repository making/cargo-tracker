package lol.maki.cargotracker.infrastructure.messaging.jms;

import lol.maki.cargotracker.application.CargoInspectionService;
import lol.maki.cargotracker.domain.model.cargo.TrackingId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

/**
 * Consumes JMS messages and delegates notification of misdirected cargo to the tracking
 * service.
 * <p>
 * This is a programmatic hook into the JMS infrastructure to make cargo inspection
 * message-driven.
 */

@Component
public class CargoHandledConsumer {

	private final CargoInspectionService cargoInspectionService;

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public CargoHandledConsumer(CargoInspectionService cargoInspectionService) {
		this.cargoInspectionService = cargoInspectionService;
	}

	@JmsListener(destination = Destinations.CARGO_HANDLED_QUEUE)
	public void onMessage(String trackingidString) {
		logger.info("CargoHandledQueue#onMessage({})", trackingidString);
		cargoInspectionService.inspectCargo(new TrackingId(trackingidString));
	}

}
