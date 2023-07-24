package se.citerus.dddsample.interfaces.tracking.api;

/**
 * A data-transport object class represnting a view of a HandlingEvent owned by a Cargo.
 * Used by the REST API for public cargo tracking.
 */
public record HandlingEventDTO(String location, String time, String type, String voyageNumber, boolean isExpected,
		String description) {

}
