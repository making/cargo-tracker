package lol.maki.cargotracker.interfaces.booking.web;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.ServletRequestDataBinder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItinerarySelectionCommandTest {

	RouteAssignmentCommand command;

	MockHttpServletRequest request;

	@Test
	void testBind() {
		command = new RouteAssignmentCommand();
		request = new MockHttpServletRequest();

		request.addParameter("legs[0].voyageNumber", "CM01");
		request.addParameter("legs[0].fromUnLocode", "AAAAA");
		request.addParameter("legs[0].toUnLocode", "BBBBB");

		request.addParameter("legs[1].voyageNumber", "CM02");
		request.addParameter("legs[1].fromUnLocode", "CCCCC");
		request.addParameter("legs[1].toUnLocode", "DDDDD");

		request.addParameter("trackingId", "XYZ");

		ServletRequestDataBinder binder = new ServletRequestDataBinder(command);
		binder.bind(request);

		assertThat(command.getLegs()).hasSize(2)
			.extracting("voyageNumber", "fromUnLocode", "toUnLocode")
			.containsAll(List.of(Tuple.tuple("CM01", "AAAAA", "BBBBB"), Tuple.tuple("CM02", "CCCCC", "DDDDD")));

		assertThat(command.getTrackingId()).isEqualTo("XYZ");
	}

}