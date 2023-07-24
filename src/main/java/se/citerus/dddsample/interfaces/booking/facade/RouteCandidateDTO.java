package se.citerus.dddsample.interfaces.booking.facade;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * DTO for presenting and selecting an itinerary from a collection of candidates.
 */
public record RouteCandidateDTO(List<LegDTO> legs) implements Serializable {

	/**
	 * Constructor.
	 * @param legs The legs for this itinerary.
	 */
	public RouteCandidateDTO {
	}

	/**
	 * @return An unmodifiable list DTOs.
	 */
	@Override
	public List<LegDTO> legs() {
		return Collections.unmodifiableList(legs);
	}

}