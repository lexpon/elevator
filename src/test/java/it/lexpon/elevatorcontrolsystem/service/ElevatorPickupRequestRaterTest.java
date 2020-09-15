package it.lexpon.elevatorcontrolsystem.service;

import static it.lexpon.elevatorcontrolsystem.domainobject.Direction.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import it.lexpon.elevatorcontrolsystem.datatransferobject.PickupRequest;
import it.lexpon.elevatorcontrolsystem.domainobject.Elevator;


class ElevatorPickupRequestRaterTest {

	private final ElevatorPickupRequestRater testee = new ElevatorPickupRequestRater();

	@Test
	public void shouldFindLowestRatedElevatorForPickupRequest() {
		// GIVEN
		Elevator elevator1 = Elevator.create(1);
		Elevator elevator2 = Elevator.create(1);
		elevator1.changeDirection(UP);
		elevator2.changeDirection(DOWN);
		elevator1.changeFloor(1);
		elevator2.changeFloor(5);

		List<Elevator> elevators = List.of(elevator1, elevator2);

		PickupRequest pickupRequest = PickupRequest.builder()
			.currentFloor(4)
			.destinationFloor(2)
			.build();

		// WHEN
		Optional<Elevator> elevatorFound = testee.findElevatorForRequest(elevators, pickupRequest);

		// THEN
		assertThat(elevatorFound).isNotEmpty();
		assertThat(elevatorFound.get()).isEqualTo(elevator2);
	}


	@Test
	public void shouldNotFindTheSameElevatorForSimilarPickupRequest() {
		// GIVEN
		Elevator elevator1 = Elevator.create(1);
		Elevator elevator2 = Elevator.create(1);
		elevator1.changeDirection(UP);
		elevator2.changeDirection(UP);
		elevator1.changeFloor(1);
		elevator2.changeFloor(1);

		List<Elevator> elevators = List.of(elevator1, elevator2);

		PickupRequest pickupRequest1 = PickupRequest.builder()
			.currentFloor(2)
			.destinationFloor(4)
			.build();
		PickupRequest pickupRequest2 = PickupRequest.builder()
			.currentFloor(2)
			.destinationFloor(4)
			.build();

		// WHEN
		Optional<Elevator> elevatorFound1 = testee.findElevatorForRequest(elevators, pickupRequest1);
		Optional<Elevator> elevatorFound2 = testee.findElevatorForRequest(elevators, pickupRequest2);

		// THEN
		assertThat(elevatorFound1).isNotEmpty();
		assertThat(elevatorFound1.get()).isEqualTo(elevator1);
		assertThat(elevatorFound2).isNotEmpty();
		assertThat(elevatorFound2.get()).isEqualTo(elevator2);
	}


	@Test
	public void shouldNotFindAnyElevator() {
		// GIVEN
		Elevator elevator1 = Elevator.create(1);
		Elevator elevator2 = Elevator.create(1);
		elevator1.changeDirection(UP);
		elevator2.changeDirection(UP);
		elevator1.changeFloor(1);
		elevator2.changeFloor(1);

		List<Elevator> elevators = List.of(elevator1, elevator2);

		PickupRequest pickupRequest = PickupRequest.builder()
			.currentFloor(4)
			.destinationFloor(2)
			.build();

		// WHEN
		Optional<Elevator> elevatorFound = testee.findElevatorForRequest(elevators, pickupRequest);

		// THEN
		assertThat(elevatorFound).isEmpty();
	}

}
