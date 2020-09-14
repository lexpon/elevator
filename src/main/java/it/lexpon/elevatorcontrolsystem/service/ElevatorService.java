package it.lexpon.elevatorcontrolsystem.service;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.lexpon.elevatorcontrolsystem.datatransferobject.ElevatorStatusResponse;
import it.lexpon.elevatorcontrolsystem.datatransferobject.PickupRequest;
import it.lexpon.elevatorcontrolsystem.domainobject.Elevator;


@Service
public class ElevatorService {

	private final List<Integer> elevatorIds;
	private final List<PickupRequest> pickupRequests;
	private final List<Elevator> elevators;

	public ElevatorService(@Value("${elevator.ids}") List<Integer> elevatorIds) {
		this.elevatorIds = elevatorIds;
		this.pickupRequests = new ArrayList<>();
		this.elevators = init();
	}


	private List<Elevator> init() {
		if (elevatorIds == null || elevatorIds.isEmpty()) {
			throw new IllegalStateException("elevatorIds have to be set. check 'elevator.ids' in properties.");
		}
		return elevatorIds.stream()
			.map(Elevator::create)
			.collect(toList());
	}


	public ElevatorStatusResponse getStatus() {
		return ElevatorStatusResponse.builder()
			.elevators(elevators)
			.build();
	}


	public void pickup(PickupRequest pickupRequest) {
		pickupRequests.add(pickupRequest);
		// TODO this is hacky. need to find the "optimal" elevator
		elevators.get(0).addRequest(pickupRequest);
	}


	public void performOneTimeStep() {
		elevators.forEach(Elevator::performMove);
	}
}
