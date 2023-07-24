package lol.maki.cargotracker.acceptance;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriverBuilder;
import org.springframework.web.context.WebApplicationContext;
import lol.maki.cargotracker.Application;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractAcceptanceTest {

	@Autowired
	private WebApplicationContext context;

	protected WebDriver driver;

	@LocalServerPort
	public int port;

	@BeforeEach
	public void setup() {
		driver = MockMvcHtmlUnitDriverBuilder.webAppContextSetup(context).build();
	}

	@AfterEach
	public void tearDown() {
		driver.quit();
	}

}