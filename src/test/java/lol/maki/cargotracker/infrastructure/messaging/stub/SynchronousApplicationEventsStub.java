package lol.maki.cargotracker.infrastructure.messaging.stub;

import lol.maki.cargotracker.application.CargoInspectionService;
import lol.maki.cargotracker.application.HandlingEventRegistrationAttempt;
import lol.maki.cargotracker.domain.model.cargo.Cargo;
import lol.maki.cargotracker.domain.model.handling.HandlingEvent;
import lol.maki.cargotracker.application.ApplicationEvents;

public class SynchronousApplicationEventsStub implements ApplicationEvents {

	CargoInspectionService cargoInspectionService;

	public void setCargoInspectionService(CargoInspectionService cargoInspectionService) {
		this.cargoInspectionService = cargoInspectionService;
	}

	@Override
	public void cargoWasHandled(HandlingEvent event) {
		System.out.println("EVENT: cargo was handled: " + event);
		cargoInspectionService.inspectCargo(event.cargo().trackingId());
	}

	@Override
	public void cargoWasMisdirected(Cargo cargo) {
		System.out.println("EVENT: cargo was misdirected");
	}

	@Override
	public void cargoHasArrived(Cargo cargo) {
		System.out.println("EVENT: cargo has arrived: " + cargo.trackingId().idString());
	}

	@Override
	public void receivedHandlingEventRegistrationAttempt(HandlingEventRegistrationAttempt attempt) {
		System.out.println("EVENT: received handling event registration attempt");
	}

}
