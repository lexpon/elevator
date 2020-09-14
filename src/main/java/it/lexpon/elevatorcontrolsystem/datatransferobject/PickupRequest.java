package it.lexpon.elevatorcontrolsystem.datatransferobject;

import static it.lexpon.elevatorcontrolsystem.domainobject.Direction.*;

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

	private final Integer currentFloor;
	private final Integer destinationFloor;

	public Direction determineDirection() {
		if (currentFloor.equals(destinationFloor)) {
			throw new RuntimeException(String.format("Cannot determine direction, because floors are the same. pickupRequest=%s", this));
		}
		else if (currentFloor < destinationFloor) {
			return UP;
		}
		else {
			return DOWN;
		}
	}

}
