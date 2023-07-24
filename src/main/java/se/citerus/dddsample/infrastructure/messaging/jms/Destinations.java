package se.citerus.dddsample.infrastructure.messaging.jms;

public final class Destinations {

	public static final String CARGO_HANDLED_QUEUE = "CargoHandledQueue";

	public static final String MISDIRECTED_CARGO_QUEUE = "MisdirectedCargoQueue";

	public static final String DELIVERED_CARGO_QUEUE = "DeliveredCargoQueue";

	public static final String HANDLING_EVENT_REGISTRATION_ATTEMPT_QUEUE = "HandlingEventRegistrationAttemptQueue";

	public static final String REJECTED_REGISTRATION_ATTEMPTS_QUEUE = "RejectedRegistrationAttemptsQueue";

}
