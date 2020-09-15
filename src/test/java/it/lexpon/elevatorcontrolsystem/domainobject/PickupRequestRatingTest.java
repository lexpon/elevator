package it.lexpon.elevatorcontrolsystem.domainobject;

import static it.lexpon.elevatorcontrolsystem.domainobject.Direction.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import it.lexpon.elevatorcontrolsystem.datatransferobject.PickupRequest;


class PickupRequestRatingTest {

	@Test
	public void shouldCalculateWeight() {

		// GIVEN
		Elevator elevator1 = Elevator.create(1);
		elevator1.changeFloor(1);
		elevator1.changeDirection(UP);
		Elevator elevator2 = Elevator.create(2);
		elevator2.changeFloor(7);
		elevator2.changeDirection(DOWN);
		Elevator elevator3 = Elevator.create(3);
		elevator3.changeFloor(5);
		elevator3.changeDirection(NONE);

		PickupRequest pickupRequest = PickupRequest.builder()
			.currentFloor(2)
			.destinationFloor(8)
			.build();

		// WHEN
		PickupRequestRating rating1 = elevator1.ratePickupRequest(pickupRequest);
		PickupRequestRating rating2 = elevator2.ratePickupRequest(pickupRequest);
		PickupRequestRating rating3 = elevator3.ratePickupRequest(pickupRequest);

		// THEN ... elevator2 should have the best rating
		assertThat(rating2).isLessThan(rating3);
		assertThat(rating2).isLessThan(rating1);
		assertThat(rating1).isLessThan(rating3);
	}
}
