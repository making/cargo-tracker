package com.pathfinder.api;

import java.io.Serializable;
import java.time.Instant;

/**
 * Represents an edge in a path through a graph, describing the route of a cargo.
 */
public record TransitEdge(String edge, String fromNode, String toNode, Instant fromDate,
		Instant toDate) implements Serializable {

	/**
	 * Constructor.
	 * @param edge
	 * @param fromNode
	 * @param toNode
	 * @param fromDate
	 * @param toDate
	 */
	public TransitEdge {
	}

}