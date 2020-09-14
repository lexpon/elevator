package it.lexpon.elevatorcontrolsystem.domainobject;

import static it.lexpon.elevatorcontrolsystem.domainobject.Direction.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import it.lexpon.elevatorcontrolsystem.datatransferobject.PickupRequest;


public class ElevatorTest {

	@Test
	public void shouldCreateElevator() {
		// GIVEN
		Integer id = 1;

		// WHEN
		Elevator elevatorActual = Elevator.create(id);

		// THEN
		assertThat(elevatorActual.getId()).isEqualTo(id);
		assertThat(elevatorActual.getCurrentFloor()).isEqualTo(0);
		assertThat(elevatorActual.getDirection()).isEqualTo(NONE);
		assertThat(elevatorActual.getPickupRequestsOpen()).isEmpty();
	}


	@Test
	public void shouldAddPickupRequest() {
		// GIVEN
		Integer id = 1;
		Elevator elevator = Elevator.create(id);
		PickupRequest pickupRequestToSend = PickupRequest.builder()
			.currentFloor(1)
			.destinationFloor(5)
			.build();

		// WHEN
		elevator.addRequest(pickupRequestToSend);

		// THEN
		assertThat(elevator.getPickupRequestsOpen().size()).isEqualTo(1);
		PickupRequest pickupRequestReceived = elevator.getPickupRequestsOpen().get(0);
		assertThat(pickupRequestReceived).isEqualTo(pickupRequestToSend);
	}


	@Test
	public void shouldChangeDirection() {
		// GIVEN
		Integer id = 1;
		Elevator elevator = Elevator.create(id);

		// WHEN
		elevator.changeDirection(UP);

		// THEN
		assertThat(elevator.getDirection()).isEqualTo(UP);
	}


	@Test
	public void shouldChangeFloor() {
		// GIVEN
		Integer id = 1;
		Elevator elevator = Elevator.create(id);

		// WHEN
		elevator.changeFloor(5);

		// THEN
		assertThat(elevator.getCurrentFloor()).isEqualTo(5);
	}


	@Test
	public void shouldMoveUp() {
		// GIVEN
		Integer id = 1;
		Elevator elevator = Elevator.create(id);
		elevator.changeFloor(5);

		// WHEN
		elevator.floorUp();

		// THEN
		assertThat(elevator.getCurrentFloor()).isEqualTo(6);
	}


	@Test
	public void shouldMoveDown() {
		// GIVEN
		Integer id = 1;
		Elevator elevator = Elevator.create(id);
		elevator.changeFloor(5);

		// WHEN
		elevator.floorDown();

		// THEN
		assertThat(elevator.getCurrentFloor()).isEqualTo(4);
	}


	@Test
	public void shouldMoveAfterFirstPickupRequestAssignment() {
		// GIVEN
		Integer id = 1;
		Elevator elevator = Elevator.create(id);
		PickupRequest pickupRequest = PickupRequest.builder()
			.currentFloor(1)
			.destinationFloor(3)
			.build();
		elevator.addRequest(pickupRequest);

		// WHEN
		elevator.performOneTimeStep();

		// THEN ... should change direction to UP
		assertThat(elevator.getDirection()).isEqualTo(UP);
		assertThat(elevator.getPickupRequestsOpen()).isEmpty();
		assertThat(elevator.getPickupRequestsInProgress().size()).isEqualTo(1);
		assertThat(elevator.getPickupRequestsInProgress().get(0)).isEqualTo(pickupRequest);

		// WHEN
		elevator.performOneTimeStep();

		// THEN ... should move one floor up
		assertThat(elevator.getDirection()).isEqualTo(UP);
		assertThat(elevator.getCurrentFloor()).isEqualTo(1);

		// WHEN
		elevator.performOneTimeStep();

		// THEN ... should move one floor up
		assertThat(elevator.getDirection()).isEqualTo(UP);
		assertThat(elevator.getCurrentFloor()).isEqualTo(2);

		// WHEN
		elevator.performOneTimeStep();

		// THEN ... should move one floor up to destination floor
		assertThat(elevator.getDirection()).isEqualTo(UP);
		assertThat(elevator.getCurrentFloor()).isEqualTo(3);

		// WHEN
		elevator.performOneTimeStep();

		// THEN ... should stop in destination floor
		assertThat(elevator.getDirection()).isEqualTo(NONE);
		assertThat(elevator.getCurrentFloor()).isEqualTo(3);
		assertThat(elevator.getPickupRequestsInProgress()).isEmpty();
	}


	@Test
	public void shouldHandleTwoPickupRequests() {
		// GIVEN
		Integer id = 1;
		Elevator elevator = Elevator.create(id);
		elevator.changeFloor(10);
		PickupRequest pickupRequest1 = PickupRequest.builder()
			.currentFloor(9)
			.destinationFloor(6)
			.build();
		PickupRequest pickupRequest2 = PickupRequest.builder()
			.currentFloor(8)
			.destinationFloor(7)
			.build();
		elevator.addRequest(pickupRequest1);
		elevator.addRequest(pickupRequest2);

		// WHEN
		elevator.performOneTimeStep();

		// THEN ... should take 1st pickupRequest and change direction to DOWN
		assertThat(elevator.getDirection()).isEqualTo(DOWN);
		assertThat(elevator.getCurrentFloor()).isEqualTo(10);
		assertThat(elevator.getPickupRequestsOpen().size()).isEqualTo(1);
		assertThat(elevator.getPickupRequestsInProgress().size()).isEqualTo(1);

		// WHEN
		elevator.performOneTimeStep();

		// THEN ... should move one floor down
		assertThat(elevator.getDirection()).isEqualTo(DOWN);
		assertThat(elevator.getCurrentFloor()).isEqualTo(9);

		// WHEN
		elevator.performOneTimeStep();

		// THEN ... should move one floor down and take the 2nd pickupRequest, because this one is at floor #9 and goes down as well
		assertThat(elevator.getDirection()).isEqualTo(DOWN);
		assertThat(elevator.getCurrentFloor()).isEqualTo(8);
		assertThat(elevator.getPickupRequestsOpen().size()).isEqualTo(0);
		assertThat(elevator.getPickupRequestsInProgress().size()).isEqualTo(2);

		// WHEN
		elevator.performOneTimeStep();

		// THEN ... should move one floor down
		assertThat(elevator.getDirection()).isEqualTo(DOWN);
		assertThat(elevator.getCurrentFloor()).isEqualTo(7);
		assertThat(elevator.getPickupRequestsOpen().size()).isEqualTo(0);
		assertThat(elevator.getPickupRequestsInProgress().size()).isEqualTo(2);

		// WHEN
		elevator.performOneTimeStep();

		// THEN ... should stop at 7th floor and finish one pickupRequest
		assertThat(elevator.getDirection()).isEqualTo(NONE);
		assertThat(elevator.getCurrentFloor()).isEqualTo(7);
		assertThat(elevator.getPickupRequestsOpen().size()).isEqualTo(0);
		assertThat(elevator.getPickupRequestsInProgress().size()).isEqualTo(1);

		// WHEN
		elevator.performOneTimeStep();

		// THEN ... should change direction to DOWN again
		assertThat(elevator.getDirection()).isEqualTo(DOWN);
		assertThat(elevator.getCurrentFloor()).isEqualTo(7);
		assertThat(elevator.getPickupRequestsOpen().size()).isEqualTo(0);
		assertThat(elevator.getPickupRequestsInProgress().size()).isEqualTo(1);

		// WHEN
		elevator.performOneTimeStep();

		// THEN ... should move one floor down to 6th floor
		assertThat(elevator.getDirection()).isEqualTo(DOWN);
		assertThat(elevator.getCurrentFloor()).isEqualTo(6);
		assertThat(elevator.getPickupRequestsOpen().size()).isEqualTo(0);
		assertThat(elevator.getPickupRequestsInProgress().size()).isEqualTo(1);

		// WHEN
		elevator.performOneTimeStep();

		// THEN ... should stop in 6th floor and finish the last pickupRequest
		assertThat(elevator.getDirection()).isEqualTo(NONE);
		assertThat(elevator.getCurrentFloor()).isEqualTo(6);
		assertThat(elevator.getPickupRequestsOpen().size()).isEqualTo(0);
		assertThat(elevator.getPickupRequestsInProgress().size()).isEqualTo(0);
	}

}
