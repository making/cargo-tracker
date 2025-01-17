package lol.maki.cargotracker.interfaces.tracking;

import lol.maki.cargotracker.infrastructure.persistence.inmemory.CargoRepositoryInMem;
import lol.maki.cargotracker.infrastructure.persistence.inmemory.HandlingEventRepositoryInMem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import lol.maki.cargotracker.interfaces.tracking.web.CargoTrackingController;
import lol.maki.cargotracker.interfaces.tracking.web.CargoTrackingViewAdapter;
import lol.maki.cargotracker.interfaces.tracking.web.TrackCommandValidator;

import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebAppConfiguration
@SpringJUnitConfig(classes = CargoTrackingControllerTest.TestConfiguration.class)
class CargoTrackingControllerTest {

	public static class TestConfiguration {

	}

	private MockMvc mockMvc;

	@BeforeEach
	void setup() throws Exception {
		CargoRepositoryInMem cargoRepository = new CargoRepositoryInMem();
		cargoRepository.setHandlingEventRepository(new HandlingEventRepositoryInMem());
		cargoRepository.init();

		CargoTrackingController controller = new CargoTrackingController(cargoRepository,
				new HandlingEventRepositoryInMem(), new FakeMessageSource(), new TrackCommandValidator());

		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/jsp/");
		resolver.setSuffix(".jsp");
		this.mockMvc = MockMvcBuilders.standaloneSetup(controller).setViewResolvers(resolver).build();
	}

	@Test
	void canGetCargo() throws Exception {
		String trackingId = "ABC";
		Map<String, Object> model = mockMvc.perform(post("/track").param("trackingId", trackingId))
			.andReturn()
			.getModelAndView()
			.getModel();
		CargoTrackingViewAdapter cargoTrackingViewAdapter = (CargoTrackingViewAdapter) model.get("cargo");
		assertThat(cargoTrackingViewAdapter.getTrackingId()).isEqualTo(trackingId);
	}

	@Test
	void cannotGetUnknownCargo() throws Exception {
		String trackingId = "UNKNOWN";
		Map<String, Object> model = mockMvc.perform(post("/track").param("trackingId", trackingId))
			.andReturn()
			.getModelAndView()
			.getModel();
		Errors errors = (Errors) model.get(BindingResult.MODEL_KEY_PREFIX + "trackCommand");
		FieldError fe = errors.getFieldError("trackingId");
		assertThat(fe.getCode()).isEqualTo("cargo.unknown_id");
		assertThat(fe.getArguments().length).isEqualTo(1);
		assertThat(fe.getArguments()[0]).isEqualTo(trackingId);
	}

	private static class FakeMessageSource implements MessageSource {

		@Override
		public String getMessage(String s, Object[] objects, String s1, Locale locale) {
			return "test";
		}

		@Override
		public String getMessage(String s, Object[] objects, Locale locale) throws NoSuchMessageException {
			return "test";
		}

		@Override
		public String getMessage(MessageSourceResolvable messageSourceResolvable, Locale locale)
				throws NoSuchMessageException {
			return "test";
		}

	}

}