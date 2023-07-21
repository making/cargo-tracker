package se.citerus.dddsample.interfaces.booking.facade.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DTO for registering and routing a cargo.
 */
public record CargoRoutingDTO(String trackingId, String origin, String finalDestination, Instant arrivalDeadline,
		boolean misrouted, List<LegDTO> legs) implements Serializable {
	public CargoRoutingDTO(String trackingId, String origin, String finalDestination, Instant arrivalDeadline,
			boolean misrouted) {
		this(trackingId, origin, finalDestination, arrivalDeadline, misrouted, new ArrayList<>());
	}

	public void addLeg(String voyageNumber, String from, String to, Instant loadTime, Instant unloadTime) {
		legs.add(new LegDTO(voyageNumber, from, to, loadTime, unloadTime));
	}

	/**
	 * @return An unmodifiable list DTOs.
	 */
	@Override
	public List<LegDTO> legs() {
		return Collections.unmodifiableList(legs);
	}

	public boolean isRouted() {
		return !legs.isEmpty();
	}

}
