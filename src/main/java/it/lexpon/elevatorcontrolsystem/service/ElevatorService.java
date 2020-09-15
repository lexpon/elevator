package it.lexpon.elevatorcontrolsystem.service;

import static it.lexpon.elevatorcontrolsystem.domainobject.Elevator.*;
import static java.math.BigInteger.*;
import static java.util.stream.Collectors.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.lexpon.elevatorcontrolsystem.datatransferobject.ElevatorStatusResponse;
import it.lexpon.elevatorcontrolsystem.datatransferobject.PickupRequest;
import it.lexpon.elevatorcontrolsystem.domainobject.Elevator;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class ElevatorService {

	private final static int MAX_ELEVATORS = 16;

	private final List<Integer> elevatorIds;
	private final ElevatorPickupRequestRater elevatorPickupRequestRater;

	private final List<PickupRequest> pickupRequests;
	private final List<Elevator> elevators;
	private BigInteger timeStep;

	public ElevatorService(
			@Value("${elevator.ids}") List<Integer> elevatorIds,
			@Autowired ElevatorPickupRequestRater elevatorPickupRequestRater) {
		this.elevatorIds = elevatorIds;
		this.elevatorPickupRequestRater = elevatorPickupRequestRater;

		this.pickupRequests = new ArrayList<>();
		this.elevators = init();
		this.timeStep = ZERO;
	}


	private List<Elevator> init() {
		if (elevatorIds == null || elevatorIds.isEmpty()) {
			throw new IllegalStateException("elevatorIds have to be set. check 'elevator.ids' in properties.");
		}
		List<Elevator> elevators = elevatorIds.stream()
			.map(Elevator::create)
			.collect(toList());
		if (elevators.size() > MAX_ELEVATORS) {
			throw new IllegalStateException(String.format("Too many elevators. Maximum of %d is allowed", MAX_ELEVATORS));
		}
		return elevators;
	}


	public ElevatorStatusResponse getStatus() {
		return ElevatorStatusResponse.builder()
			.elevators(elevators)
			.pickupRequestsOpen(pickupRequests)
			.timeStep(timeStep)
			.build();
	}


	public void pickup(PickupRequest pickupRequest) {
		int maxOpenRequests = elevators.size() * MAX_OPEN_PICKUP_REQUESTS;
		if (pickupRequests.size() >= maxOpenRequests) {
			throw new IllegalStateException(String.format("Too many pickupRequests. Can handle maximum %d requests.", maxOpenRequests));
		}
		pickupRequests.add(pickupRequest);
		assignPickupRequests();
	}


	public void performOneTimeStep() {
		assignPickupRequests();
		log.info("Performing one time step for each elevator");
		elevators.forEach(Elevator::performOneTimeStep);
		timeStep = timeStep.add(ONE);
	}


	private void assignPickupRequests() {
		log.info("Trying to assign pickupRequests to elevators");

		List<PickupRequest> requestsAssigned = new ArrayList<>();

		pickupRequests.forEach(pickupRequest -> elevatorPickupRequestRater.findElevatorForRequest(elevators, pickupRequest)
			.ifPresentOrElse(
				elevator -> {
					log.info("Assign pickupRequest to elevator. pickUpRequest={}, elevator={}", pickupRequest, elevator);
					elevator.addRequest(pickupRequest);
					requestsAssigned.add(pickupRequest);
				},
				() -> log.info("No suitable elevator found for pickupRequest. Waiting for one to be free. pickupRequest={}", pickupRequest)));

		pickupRequests.removeAll(requestsAssigned);
	}

}
