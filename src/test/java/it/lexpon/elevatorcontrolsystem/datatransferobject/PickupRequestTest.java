package it.lexpon.elevatorcontrolsystem.datatransferobject;

import static it.lexpon.elevatorcontrolsystem.domainobject.Direction.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import it.lexpon.elevatorcontrolsystem.domainobject.Direction;


public class PickupRequestTest {

	@Test
	public void shouldBeDirectionUp() {
		// GIVEN
		PickupRequest pickupRequest = PickupRequest.builder()
			.currentFloor(1)
			.destinationFloor(2)
			.build();

		// WHEN
		Direction directionActual = pickupRequest.direction();

		// THEN
		assertThat(directionActual).isEqualTo(UP);
	}


	@Test
	public void shouldBeDirectionDown() {
		// GIVEN
		PickupRequest pickupRequest = PickupRequest.builder()
			.currentFloor(2)
			.destinationFloor(1)
			.build();

		// WHEN
		Direction directionActual = pickupRequest.direction();

		// THEN
		assertThat(directionActual).isEqualTo(DOWN);
	}


	@Test
	public void shouldThrowExceptionSameFloors() {
		// THEN 
		Exception exception = assertThrows(IllegalStateException.class,

			// WHEN
			() -> PickupRequest.builder()
				.currentFloor(1)
				.destinationFloor(1).build());

		assertThat(exception.getMessage()).contains("Floor numbers cannot be the same for pickupRequest ");
	}


	@Test
	public void shouldThrowExceptionFloorsOutOfRange() {
		// GIVEN
		List<Integer> wrongFloors = List.of(-1, 11);

		// ... current floor
		wrongFloors.forEach(wrongFloor -> {

			// THEN
			Exception exception = assertThrows(IllegalStateException.class,

				// WHEN
				() -> PickupRequest.builder()
					.currentFloor(wrongFloor)
					.destinationFloor(1)
					.build());

			assertThat(exception.getMessage()).contains("Floor number has to be in range ");
		});

		// ... destination floor
		wrongFloors.forEach(wrongFloor -> {

			// THEN
			Exception exception = assertThrows(IllegalStateException.class,

				// WHEN
				() -> PickupRequest.builder()
					.currentFloor(1)
					.destinationFloor(wrongFloor)
					.build());

			assertThat(exception.getMessage()).contains("Floor number has to be in range ");
		});
	}
}
