package it.lexpon.elevatorcontrolsystem.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import it.lexpon.elevatorcontrolsystem.datatransferobject.PickupRequest;
import it.lexpon.elevatorcontrolsystem.domainobject.Elevator;


@Service
public class ElevatorPickupRequestRater {

	public Optional<Elevator> findElevatorForRequest(List<Elevator> elevators, PickupRequest pickupRequest) {
		return elevators.stream()
			.map(elevator -> Pair.of(elevator, elevator.ratePickupRequest(pickupRequest)))
			.sorted(Comparator.comparingInt(pair -> pair.getRight().weight()))
			.filter(pair -> pair.getRight().weight() >= 0)
			.filter(pair -> !pair.getLeft().getPickupRequestsOpen().contains(pickupRequest))
			.findFirst()
			.map(Pair::getLeft);
	}

}
