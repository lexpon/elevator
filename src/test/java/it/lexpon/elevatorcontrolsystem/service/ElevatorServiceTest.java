package it.lexpon.elevatorcontrolsystem.service;

import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import it.lexpon.elevatorcontrolsystem.datatransferobject.ElevatorStatusResponse;
import it.lexpon.elevatorcontrolsystem.datatransferobject.PickupRequest;
import it.lexpon.elevatorcontrolsystem.domainobject.Elevator;


public class ElevatorServiceTest {

	private final List<Integer> elevatorIds = List.of(1, 2, 3);
	private final ElevatorPickupRequestRater elevatorPickupRequestRaterMock = mock(ElevatorPickupRequestRater.class);

	private final ElevatorService testee = new ElevatorService(elevatorIds, elevatorPickupRequestRaterMock);

	@Test
	public void shouldAcceptAndAssignPickupRequest() throws Exception {
		// GIVEN
		PickupRequest request = PickupRequest.builder()
			.currentFloor(1)
			.destinationFloor(5)
			.build();

		Elevator elevatorRated = testee.getStatus().getElevators().stream().findAny().orElseThrow(() -> new Exception("testee has no elevators"));

		when(elevatorPickupRequestRaterMock.findElevatorForRequest(anyList(), eq(request)))
			.thenReturn(Optional.of(elevatorRated));

		// WHEN
		testee.pickup(request);

		// THEN
		ElevatorStatusResponse status = testee.getStatus();
		assertThat(status.getPickupRequestsOpen().size()).isEqualTo(0);

		List<Elevator> elevatorsWithOpenRequests = status.getElevators().stream()
			.filter(elevator -> elevator.getPickupRequestsOpen().size() > 0)
			.collect(toList());
		assertThat(elevatorsWithOpenRequests.size()).isEqualTo(1);

		Elevator elevatorWithRequest = elevatorsWithOpenRequests.get(0);
		assertThat(elevatorWithRequest).isEqualTo(elevatorRated);
		assertThat(elevatorWithRequest.getPickupRequestsOpen().size()).isEqualTo(1);
		assertThat(elevatorWithRequest.getPickupRequestsOpen().get(0)).isEqualTo(request);

		verify(elevatorPickupRequestRaterMock, times(1)).findElevatorForRequest(anyList(), eq(request));
		verifyNoMoreInteractions(elevatorPickupRequestRaterMock);
	}


	@Test
	public void shouldNotAssignPickupRequestNoElevatorAvailable() {
		// GIVEN
		PickupRequest request = PickupRequest.builder()
			.currentFloor(1)
			.destinationFloor(5)
			.build();

		when(elevatorPickupRequestRaterMock.findElevatorForRequest(anyList(), any(PickupRequest.class)))
			.thenReturn(Optional.empty());

		// WHEN
		testee.pickup(request);

		// THEN
		ElevatorStatusResponse status = testee.getStatus();
		assertThat(status.getPickupRequestsOpen().size()).isEqualTo(1);

		List<Elevator> elevatorsWithOpenRequests = status.getElevators().stream()
			.filter(elevator -> elevator.getPickupRequestsOpen().size() > 0)
			.collect(toList());
		assertThat(elevatorsWithOpenRequests.size()).isEqualTo(0);

		verify(elevatorPickupRequestRaterMock, times(1)).findElevatorForRequest(anyList(), eq(request));
		verifyNoMoreInteractions(elevatorPickupRequestRaterMock);
	}

}
