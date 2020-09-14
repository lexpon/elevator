package it.lexpon.elevatorcontrolsystem.domainobject;

import static it.lexpon.elevatorcontrolsystem.domainobject.Direction.NONE;
import static lombok.AccessLevel.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import it.lexpon.elevatorcontrolsystem.datatransferobject.PickupRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;


@Builder(access = PRIVATE)
@Getter
@ToString
@Slf4j
public class Elevator {

	private static final int MAX_FLOOR_NUMBER = 10;
	private static final int MAX_PLACES = 8;

	private final Integer id;
	private Integer currentFloor;
	private Direction direction;
	private final List<PickupRequest> pickupRequests;

	public static Elevator create(Integer id) {
		return Elevator.builder()
			.id(id)
			.currentFloor(0)
			.direction(NONE)
			.pickupRequests(new ArrayList<>())
			.build();
	}


	public void addRequest(PickupRequest pickupRequest) {
		pickupRequests.add(pickupRequest);
	}


	public Optional<PickupRequest> getTopPickupRequest() {
		return pickupRequests.size() > 0
				? Optional.of(pickupRequests.get(0))
				: Optional.empty();
	}


	public void changeDirection(Direction newDirection) {
		log.info("Change direction of elevator={} to newDirection={}", this, newDirection);
		direction = newDirection;
	}


	public void changeFloor(Integer newFloor) {
		currentFloor = newFloor;
	}


	public void floorUp() {
		if (currentFloor < MAX_FLOOR_NUMBER) {
			log.info("Moving elevator one floor up. {}", this);
			currentFloor++;
		}
		throw new RuntimeException(String.format("Floor cannot be bigger than %d", MAX_FLOOR_NUMBER));
	}


	public void floorDown() {
		if (currentFloor > 0) {
			log.info("Moving elevator one floor down. {}", this);
			currentFloor--;
		}
		throw new RuntimeException("Floor cannot get negative");
	}


	public boolean isInMotion() {
		return direction != NONE;
	}

}
