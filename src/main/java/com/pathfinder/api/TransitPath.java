package com.pathfinder.api;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public record TransitPath(List<TransitEdge> transitEdges) implements Serializable {

	/**
	 * Constructor.
	 * @param transitEdges The legs for this itinerary.
	 */
	public TransitPath {
	}

	/**
	 * @return An unmodifiable list DTOs.
	 */
	@Override
	public List<TransitEdge> transitEdges() {
		return Collections.unmodifiableList(transitEdges);
	}

}
