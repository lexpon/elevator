package it.lexpon.elevatorcontrolsystem.service;

import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import it.lexpon.elevatorcontrolsystem.datatransferobject.ElevatorStatusResponse;
import it.lexpon.elevatorcontrolsystem.datatransferobject.PickupRequest;
import it.lexpon.elevatorcontrolsystem.domainobject.Elevator;


public class ElevatorServiceTest {

	private final List<Integer> elevatorIds = List.of(16);

	private final ElevatorService testee = new ElevatorService(elevatorIds);

	@Test
	public void shouldAcceptAndAssignPickupRequest() {
		// GIVEN
		PickupRequest request = PickupRequest.builder()
			.currentFloor(1)
			.destinationFloor(5)
			.build();

		// WHEN
		testee.pickup(request);

		// THEN
		ElevatorStatusResponse status = testee.getStatus();
		List<Elevator> elevatorsWithOpenRequests = status.getElevators().stream()
			.filter(elevator -> elevator.getPickupRequestsOpen().size() > 0)
			.collect(toList());
		assertThat(elevatorsWithOpenRequests.size()).isEqualTo(1);
		Elevator elevatorWithRequest = elevatorsWithOpenRequests.get(0);
		assertThat(elevatorWithRequest.getPickupRequestsOpen().size()).isEqualTo(1);
		assertThat(elevatorWithRequest.getPickupRequestsOpen().get(0)).isEqualTo(request);
	}

}
