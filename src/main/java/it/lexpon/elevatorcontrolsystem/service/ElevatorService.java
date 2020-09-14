package it.lexpon.elevatorcontrolsystem.service;

import static it.lexpon.elevatorcontrolsystem.domainobject.Elevator.*;
import static java.math.BigInteger.*;
import static java.util.stream.Collectors.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.lexpon.elevatorcontrolsystem.datatransferobject.ElevatorStatusResponse;
import it.lexpon.elevatorcontrolsystem.datatransferobject.PickupRequest;
import it.lexpon.elevatorcontrolsystem.domainobject.Elevator;
import it.lexpon.elevatorcontrolsystem.domainobject.ElevatorRated;
import it.lexpon.elevatorcontrolsystem.domainobject.PickupRequestRating;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class ElevatorService {

	private final static int MAX_ELEVATORS = 16;

	private final List<Integer> elevatorIds;
	private final List<PickupRequest> pickupRequests;
	private final List<Elevator> elevators;
	private BigInteger timeStep;

	public ElevatorService(@Value("${elevator.ids}") List<Integer> elevatorIds) {
		this.elevatorIds = elevatorIds;
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

		pickupRequests.forEach(
			pickupRequest -> {
				Optional<ElevatorRated> elevatorForRequest = findElevatorForRequest(pickupRequest);
				if (elevatorForRequest.isPresent()) {
					Elevator elevator = elevatorForRequest.get().getElevator();
					log.info("Assign pickupRequest to elevator. pickUpRequest={}, elevator={}", pickupRequest, elevator);
					elevator.addRequest(pickupRequest);
					requestsAssigned.add(pickupRequest);
				}
				else {
					log.info("No free elevator found for pickupRequest. Waiting for one to be free. pickupRequest={}", pickupRequest);
				}
			});

		pickupRequests.removeAll(requestsAssigned);
	}


	private Optional<ElevatorRated> findElevatorForRequest(PickupRequest pickupRequest) {
		return elevators.stream()
			.map(elevator -> {
				PickupRequestRating pickupRequestRating = elevator.ratePickupRequest(pickupRequest);
				return ElevatorRated.builder()
					.elevator(elevator)
					.pickupRequestRating(pickupRequestRating)
					.build();
			})
			.sorted(Comparator.comparingInt(ElevatorRated::getWeight))
			.filter(elevatorRated -> elevatorRated.getWeight() >= 0)
			.filter(elevatorRated -> !elevatorRated.getElevator().getPickupRequestsOpen().contains(pickupRequest))
			.findFirst();
	}
}
