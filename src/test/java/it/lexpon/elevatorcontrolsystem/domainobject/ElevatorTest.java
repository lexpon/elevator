package it.lexpon.elevatorcontrolsystem.domainobject;

import static it.lexpon.elevatorcontrolsystem.domainobject.Direction.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

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
		assertThat(elevatorActual.getPickupRequestsReceived()).isEmpty();
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
		assertThat(elevator.getPickupRequestsReceived().size()).isEqualTo(1);
		PickupRequest pickupRequestReceived = elevator.getPickupRequestsReceived().get(0);
		assertThat(pickupRequestReceived).isEqualTo(pickupRequestToSend);
	}


	@Test
	public void shouldGetTopPickupRequest() {
		// GIVEN
		Integer id = 1;
		Elevator elevator = Elevator.create(id);
		PickupRequest pickupRequest1st = PickupRequest.builder()
			.currentFloor(1)
			.destinationFloor(5)
			.build();
		PickupRequest pickupRequest2nd = PickupRequest.builder()
			.currentFloor(2)
			.destinationFloor(3)
			.build();
		elevator.addRequest(pickupRequest1st);
		elevator.addRequest(pickupRequest2nd);

		// WHEN
		Optional<PickupRequest> topPickupRequest = elevator.getTopPickupRequest();

		// THEN
		assertThat(topPickupRequest).isNotEmpty();
		assertThat(topPickupRequest.get()).isEqualTo(pickupRequest1st);
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
	public void shouldBeInMotion() {
		// GIVEN
		Integer id = 1;
		Elevator elevator = Elevator.create(id);
		elevator.changeDirection(UP);

		// WHEN
		boolean inMotion = elevator.isInMotion();

		assertThat(inMotion).isTrue();
	}


	@Test
	public void shouldNotBeInMotion() {
		// GIVEN
		Integer id = 1;
		Elevator elevator = Elevator.create(id);

		// WHEN
		boolean inMotion = elevator.isInMotion();

		assertThat(inMotion).isFalse();
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
		elevator.performMove();

		// THEN ... should change direction to UP
		assertThat(elevator.getDirection()).isEqualTo(UP);
		assertThat(elevator.getPickupRequestsReceived()).isEmpty();
		assertThat(elevator.getPickupRequestsInProgress()).isNotEmpty();
		assertThat(elevator.getPickupRequestsInProgress().get(0)).isEqualTo(pickupRequest);

		// WHEN
		elevator.performMove();

		// THEN ... should move one floor up
		assertThat(elevator.getDirection()).isEqualTo(UP);
		assertThat(elevator.getCurrentFloor()).isEqualTo(1);

		// WHEN
		elevator.performMove();

		// THEN ... should move one floor up
		assertThat(elevator.getDirection()).isEqualTo(UP);
		assertThat(elevator.getCurrentFloor()).isEqualTo(2);

		// WHEN
		elevator.performMove();

		// THEN ... should move one floor up to destination floor
		assertThat(elevator.getDirection()).isEqualTo(UP);
		assertThat(elevator.getCurrentFloor()).isEqualTo(3);

		// WHEN
		elevator.performMove();

		// THEN ... should stop in destination floor
		assertThat(elevator.getDirection()).isEqualTo(NONE);
		assertThat(elevator.getCurrentFloor()).isEqualTo(3);
		assertThat(elevator.getPickupRequestsInProgress()).isEmpty();
	}

}
