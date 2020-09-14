package it.lexpon.elevatorcontrolsystem.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;
import static org.springframework.http.HttpStatus.*;

import java.net.URL;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import it.lexpon.elevatorcontrolsystem.datatransferobject.ElevatorStatusResponse;
import it.lexpon.elevatorcontrolsystem.datatransferobject.PickupRequest;


@SpringBootTest(webEnvironment = RANDOM_PORT)
class ElevatorControllerIntegrationTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void shouldGetElevatorStatus() throws Exception {
		// WHEN
		String url = new URL("http://localhost:" + port + "/api/v1/elevator").toString();
		ResponseEntity<ElevatorStatusResponse> response = restTemplate.getForEntity(url, ElevatorStatusResponse.class);

		// THEN
		assertThat(response.getStatusCode()).isEqualTo(OK);
	}


	@Test
	public void shouldPostPickupRequest() throws Exception {
		// GIVEN
		PickupRequest pickupRequest = PickupRequest.builder()
			.currentFloor(2)
			.destinationFloor(7)
			.build();

		HttpEntity<PickupRequest> request = new HttpEntity<>(pickupRequest);

		// WHEN
		String url = new URL("http://localhost:" + port + "/api/v1/elevator/pickup").toString();
		ResponseEntity<ElevatorStatusResponse> response = restTemplate.postForEntity(url, request, ElevatorStatusResponse.class);

		// THEN
		assertThat(response.getStatusCode()).isEqualTo(OK);
	}


	@Test
	public void shouldPerformStep() throws Exception {
		// WHEN
		String url = new URL("http://localhost:" + port + "/api/v1/elevator/step").toString();
		ResponseEntity<ElevatorStatusResponse> response = restTemplate.postForEntity(url, null, ElevatorStatusResponse.class);

		// THEN
		assertThat(response.getStatusCode()).isEqualTo(OK);
	}

}
