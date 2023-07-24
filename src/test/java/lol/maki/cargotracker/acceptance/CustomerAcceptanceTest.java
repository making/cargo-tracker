package lol.maki.cargotracker.acceptance;

import lol.maki.cargotracker.acceptance.pages.CustomerPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

class CustomerAcceptanceTest extends AbstractAcceptanceTest {

	private CustomerPage customerPage;

	@BeforeEach
	void goToCustomerPage() {
		customerPage = new CustomerPage(driver, port);
	}

	@DirtiesContext
	@Test
	void customerSiteCanTrackValidCargo() {
		customerPage.trackCargoWithIdOf("ABC123");
		customerPage.expectCargoLocation("New York");
	}

	@DirtiesContext
	@Test
	void customerSiteErrorsOnInvalidCargo() {
		customerPage.trackCargoWithIdOf("XXX999");
		customerPage.expectErrorFor("Unknown tracking id");
	}

	@DirtiesContext
	@Test
	void customerSiteNotifiesOnMisdirectedCargo() {
		customerPage.trackCargoWithIdOf("JKL567");
		customerPage.expectNotificationOf("Cargo is misdirected");
	}

}
