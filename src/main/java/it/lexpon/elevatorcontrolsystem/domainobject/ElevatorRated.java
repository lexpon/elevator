package it.lexpon.elevatorcontrolsystem.domainobject;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;


@Builder
@Getter
@ToString
public class ElevatorRated {

	private final Elevator elevator;
	private final PickupRequestRating pickupRequestRating;

	public Integer getWeight() {
		return pickupRequestRating.weight();
	}
}
