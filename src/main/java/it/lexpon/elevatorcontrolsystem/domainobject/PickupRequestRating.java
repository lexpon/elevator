package it.lexpon.elevatorcontrolsystem.domainobject;

import static it.lexpon.elevatorcontrolsystem.domainobject.Elevator.*;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;


@Builder
@Getter
@ToString
@EqualsAndHashCode
public class PickupRequestRating implements Comparable<PickupRequestRating> {

	private final Integer floorDistance;
	private final Boolean sameDirection;
	private final Boolean elevatorStanding;
	private final Integer numberOfPickupRequestsOpen;
	private final Integer numberOfPickupRequestsInProgress;

	public Integer weight() {
		int weight = 0;
		weight = weight + floorDistance;
		if (elevatorStanding) {
			weight = weight - 1;
		}
		else if (!sameDirection) {
			weight = weight - MAX_FLOOR_NUMBER;
		}
		if (numberOfPickupRequestsOpen != null) {
			weight = weight + numberOfPickupRequestsOpen * 5;
		}
		if (numberOfPickupRequestsInProgress != null) {
			weight = weight + numberOfPickupRequestsInProgress * 2;
		}
		return weight;
	}


	@Override
	public int compareTo(PickupRequestRating o) {
		return Integer.compare(weight(), o.weight());
	}
}
