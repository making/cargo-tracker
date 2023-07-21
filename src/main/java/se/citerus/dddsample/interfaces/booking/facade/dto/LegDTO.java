package se.citerus.dddsample.interfaces.booking.facade.dto;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for a leg in an itinerary.
 */
public record LegDTO(String voyageNumber, String from, String to, Instant loadTime,
		Instant unloadTime) implements Serializable {

	/**
	 * Constructor.
	 * @param voyageNumber
	 * @param from
	 * @param to
	 * @param loadTime
	 * @param unloadTime
	 */
	public LegDTO {
	}

}
