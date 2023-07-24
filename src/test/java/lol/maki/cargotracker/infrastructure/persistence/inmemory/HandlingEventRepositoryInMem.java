package lol.maki.cargotracker.infrastructure.persistence.inmemory;

import lol.maki.cargotracker.domain.model.cargo.TrackingId;
import lol.maki.cargotracker.domain.model.handling.HandlingEvent;
import lol.maki.cargotracker.domain.model.handling.HandlingEventRepository;
import lol.maki.cargotracker.domain.model.handling.HandlingHistory;

import java.util.*;

public class HandlingEventRepositoryInMem implements HandlingEventRepository {

	private final Map<TrackingId, List<HandlingEvent>> eventMap = new HashMap<>();

	@Override
	public void store(HandlingEvent event) {
		final TrackingId trackingId = event.cargo().trackingId();
		List<HandlingEvent> list = eventMap.computeIfAbsent(trackingId, k -> new ArrayList<>());
		list.add(event);
	}

	@Override
	public HandlingHistory lookupHandlingHistoryOfCargo(TrackingId trackingId) {
		List<HandlingEvent> events = eventMap.get(trackingId);
		if (events == null)
			events = Collections.emptyList();

		return new HandlingHistory(events);
	}

}
