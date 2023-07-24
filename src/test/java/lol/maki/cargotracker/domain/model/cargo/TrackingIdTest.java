package lol.maki.cargotracker.domain.model.cargo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TrackingIdTest {

	@Test
	void testConstructor() {
		assertThatThrownBy(() -> new TrackingId(null)).isInstanceOf(NullPointerException.class);
	}

}
