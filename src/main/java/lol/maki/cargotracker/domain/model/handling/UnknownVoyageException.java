package lol.maki.cargotracker.domain.model.handling;

import lol.maki.cargotracker.domain.model.voyage.VoyageNumber;

/**
 * Thrown when trying to register an event with an unknown carrier movement id.
 */
public class UnknownVoyageException extends CannotCreateHandlingEventException {

	private final VoyageNumber voyageNumber;

	public UnknownVoyageException(VoyageNumber voyageNumber) {
		this.voyageNumber = voyageNumber;
	}

	@Override
	public String getMessage() {
		return "No voyage with number " + voyageNumber.idString() + " exists in the system";
	}

}
