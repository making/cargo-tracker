package lol.maki.cargotracker.acceptance;

import lol.maki.cargotracker.acceptance.pages.AdminPage;
import lol.maki.cargotracker.acceptance.pages.CargoBookingPage;
import lol.maki.cargotracker.acceptance.pages.CargoDestinationPage;
import lol.maki.cargotracker.acceptance.pages.CargoDetailsPage;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;

import static org.assertj.core.api.Java6Assertions.assertThat;

class AdminAcceptanceTest extends AbstractAcceptanceTest {

	@DirtiesContext
	@Test
	void adminSiteCargoListContainsCannedCargo() {
		AdminPage page = new AdminPage(driver, port);
		page.listAllCargo();

		assertThat(page.listedCargoContains("ABC123")).isTrue().withFailMessage("Cargo list doesn't contain ABC123");
		assertThat(page.listedCargoContains("JKL567")).isTrue().withFailMessage("Cargo list doesn't contain JKL567");
	}

	@DirtiesContext
	@Test
	void adminSiteCanBookNewCargo() {
		AdminPage adminPage = new AdminPage(driver, port);

		CargoBookingPage cargoBookingPage = adminPage.bookNewCargo();
		cargoBookingPage.selectOrigin("NLRTM");
		cargoBookingPage.selectDestination("USDAL");
		LocalDate arrivalDeadline = LocalDate.now().plusWeeks(3);
		cargoBookingPage.selectArrivalDeadline(arrivalDeadline);
		CargoDetailsPage cargoDetailsPage = cargoBookingPage.book();

		String newCargoTrackingId = cargoDetailsPage.getTrackingId();
		adminPage = cargoDetailsPage.listAllCargo();
		assertThat(adminPage.listedCargoContains(newCargoTrackingId)).isTrue()
			.withFailMessage("Cargo list doesn't contain %s", newCargoTrackingId);

		cargoDetailsPage = adminPage.showDetailsFor(newCargoTrackingId);
		cargoDetailsPage.expectOriginOf("NLRTM");
		cargoDetailsPage.expectDestinationOf("USDAL");

		CargoDestinationPage cargoDestinationPage = cargoDetailsPage.changeDestination();
		cargoDetailsPage = cargoDestinationPage.selectDestinationTo("AUMEL");
		cargoDetailsPage.expectDestinationOf("AUMEL");
		cargoDetailsPage.expectArrivalDeadlineOf(arrivalDeadline);
	}

}
