package se.citerus.dddsample.interfaces.tracking.ws;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;
import se.citerus.dddsample.Application;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CargoTrackingRestControllerIntegrationTest {

	@LocalServerPort
	private int port;

	private final RestTemplate restTemplate = new RestTemplate();

	@Transactional
	@Test
	void shouldReturn200ResponseAndJsonWhenRequestingCargoWithIdABC123() throws Exception {
		URI uri = new UriTemplate("http://localhost:{port}/api/track/ABC123").expand(port);
		RequestEntity<Void> request = RequestEntity.get(uri).build();

		ResponseEntity<JsonNode> response = restTemplate.exchange(request, JsonNode.class);

		assertThat(response.getStatusCodeValue()).isEqualTo(200);
		JsonNode expected = new ObjectMapper()
			.readValue(getClass().getResourceAsStream("/sampleCargoTrackingResponse.json"), JsonNode.class);
		assertThat(response.getHeaders().get("Content-Type")).containsExactly("application/json");
		assertThat(response.getBody()).isEqualTo(expected);
	}

	@Test
	void shouldReturnValidationErrorResponseWhenInvalidHandlingReportIsSubmitted() throws Exception {
		URI uri = new UriTemplate("http://localhost:{port}/api/track/MISSING").expand(port);
		RequestEntity<Void> request = RequestEntity.get(uri).build();

		try {
			restTemplate.exchange(request, String.class);
			fail("Did not throw HttpClientErrorException");
		}
		catch (HttpClientErrorException e) {
			assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		}
	}

}
