package it.lexpon.elevatorcontrolsystem.datatransferobject;

import static it.lexpon.elevatorcontrolsystem.domainobject.Direction.*;
import static it.lexpon.elevatorcontrolsystem.domainobject.Elevator.*;

import java.util.UUID;

import it.lexpon.elevatorcontrolsystem.domainobject.Direction;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;


@Builder
@Getter
@ToString
@EqualsAndHashCode
public class PickupRequest {

	private final UUID id = UUID.randomUUID();
	private final Integer currentFloor;
	private final Integer destinationFloor;

	private PickupRequest(Integer currentFloor, Integer destinationFloor) {
		this.currentFloor = currentFloor;
		this.destinationFloor = destinationFloor;
		validate();
	}


	private void validate() {
		if (currentFloor < MIN_FLOOR_NUMBER
				|| currentFloor > MAX_FLOOR_NUMBER
				|| destinationFloor < MIN_FLOOR_NUMBER
				|| destinationFloor > MAX_FLOOR_NUMBER) {
			throw new IllegalStateException(String.format("Floor number has to be in range %d..%d", MIN_FLOOR_NUMBER, MAX_FLOOR_NUMBER));
		}

		if (currentFloor.equals(destinationFloor)) {
			throw new IllegalStateException(String.format("Floor numbers cannot be the same for pickupRequest %s", this));
		}
	}


	public Direction direction() {
		if (currentFloor < destinationFloor) {
			return UP;
		}
		else {
			return DOWN;
		}
	}

}
