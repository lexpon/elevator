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
		assertThat(elevatorActual.getPickupRequests()).isEmpty();
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
		assertThat(elevator.getPickupRequests().size()).isEqualTo(1);
		PickupRequest pickupRequestReceived = elevator.getPickupRequests().get(0);
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

}
