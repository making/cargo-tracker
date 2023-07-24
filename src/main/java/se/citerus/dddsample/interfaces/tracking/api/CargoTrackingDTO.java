package se.citerus.dddsample.interfaces.tracking.api;

import java.util.List;

/**
 * A data-transport object class representing a view of a Cargo entity. Used by the REST
 * API for public cargo tracking.
 */
public record CargoTrackingDTO(String trackingId, String statusText, String destination, String eta,
		String nextExpectedActivity, boolean isMisdirected, List<HandlingEventDTO> handlingEvents) {

}
