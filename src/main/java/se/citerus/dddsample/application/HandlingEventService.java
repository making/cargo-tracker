package se.citerus.dddsample.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.CannotCreateHandlingEventException;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventFactory;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;

import java.lang.invoke.MethodHandles;
import java.time.Instant;

/**
 * Handling event service.
 */
@Service
public class HandlingEventService {

	private final ApplicationEvents applicationEvents;

	private final HandlingEventRepository handlingEventRepository;

	private final HandlingEventFactory handlingEventFactory;

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public HandlingEventService(final HandlingEventRepository handlingEventRepository,
			final ApplicationEvents applicationEvents, final HandlingEventFactory handlingEventFactory) {
		this.handlingEventRepository = handlingEventRepository;
		this.applicationEvents = applicationEvents;
		this.handlingEventFactory = handlingEventFactory;
	}

	/**
	 * Registers a handling event in the system, and notifies interested parties that a
	 * cargo has been handled.
	 * @param completionTime when the event was completed
	 * @param trackingId cargo tracking id
	 * @param voyageNumber voyage number
	 * @param unLocode UN locode for the location where the event occurred
	 * @param type type of event
	 * @throws se.citerus.dddsample.domain.model.handling.CannotCreateHandlingEventException
	 * if a handling event that represents an actual event that's relevant to a cargo
	 * we're tracking can't be created from the parameters
	 */
	@Transactional(rollbackFor = CannotCreateHandlingEventException.class)
	public void registerHandlingEvent(final Instant completionTime, final TrackingId trackingId,
			final VoyageNumber voyageNumber, final UnLocode unLocode, final HandlingEvent.Type type)
			throws CannotCreateHandlingEventException {
		final Instant registrationTime = Instant.now();
		/*
		 * Using a factory to create a HandlingEvent (aggregate). This is where it is
		 * determined whether the incoming data, the attempt, actually is capable of
		 * representing a real handling event.
		 */
		final HandlingEvent event = handlingEventFactory.createHandlingEvent(registrationTime, completionTime,
				trackingId, voyageNumber, unLocode, type);

		/*
		 * Store the new handling event, which updates the persistent state of the
		 * handling event aggregate (but not the cargo aggregate - that happens
		 * asynchronously!)
		 */
		handlingEventRepository.store(event);

		/* Publish an event stating that a cargo has been handled. */
		applicationEvents.cargoWasHandled(event);

		logger.info("Registered handling event: {}", event);
	}

}
