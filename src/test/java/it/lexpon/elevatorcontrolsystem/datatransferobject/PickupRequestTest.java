package it.lexpon.elevatorcontrolsystem.datatransferobject;

import static it.lexpon.elevatorcontrolsystem.domainobject.Direction.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

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
		Direction directionActual = pickupRequest.determineDirection();

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
		Direction directionActual = pickupRequest.determineDirection();

		// THEN
		assertThat(directionActual).isEqualTo(DOWN);
	}


	@Test
	public void shouldThrowExceptionWrongFloors() {
		// GIVEN
		PickupRequest pickupRequest = PickupRequest.builder()
			.currentFloor(1)
			.destinationFloor(1)
			.build();

		// THEN 
		Exception exception = assertThrows(RuntimeException.class,
			// WHEN
			pickupRequest::determineDirection);

		assertThat(exception.getMessage()).contains("Cannot determine direction, because floors are the same. pickupRequest=");
	}
}
