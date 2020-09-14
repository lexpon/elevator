package it.lexpon.elevatorcontrolsystem.controller;

import java.util.stream.IntStream;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.lexpon.elevatorcontrolsystem.datatransferobject.ElevatorStatusResponse;
import it.lexpon.elevatorcontrolsystem.datatransferobject.PickupRequest;
import it.lexpon.elevatorcontrolsystem.service.ElevatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api/v1/elevator")
@Slf4j
@RequiredArgsConstructor
public class ElevatorController {

	private final ElevatorService elevatorService;

	@GetMapping
	public ElevatorStatusResponse getStatus() {
		return elevatorService.getStatus();
	}


	@PostMapping("pickup")
	public ElevatorStatusResponse pickup(@RequestBody PickupRequest pickupRequest) {
		elevatorService.pickup(pickupRequest);
		return elevatorService.getStatus();
	}


	@PostMapping("/step")
	public ElevatorStatusResponse performStep(@RequestParam(required = false, defaultValue = "1") Integer numberOfSteps) {
		log.info("Received step request for {} steps", numberOfSteps);

		IntStream
			.range(0, numberOfSteps)
			.forEach(step -> elevatorService.performOneTimeStep());

		return elevatorService.getStatus();
	}

}
