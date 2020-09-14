package it.lexpon.elevatorcontrolsystem.domainobject;

import static it.lexpon.elevatorcontrolsystem.domainobject.Direction.*;
import static it.lexpon.elevatorcontrolsystem.domainobject.Direction.NONE;
import static java.util.stream.Collectors.*;
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

	private final Integer id;
	private Integer currentFloor;
	private Direction direction;
	private final List<PickupRequest> pickupRequestsReceived;
	private final List<PickupRequest> pickupRequestsInProgress;

	public static Elevator create(Integer id) {
		return Elevator.builder()
			.id(id)
			.currentFloor(0)
			.direction(NONE)
			.pickupRequestsReceived(new ArrayList<>())
			.pickupRequestsInProgress(new ArrayList<>())
			.build();
	}


	public void addRequest(PickupRequest pickupRequest) {
		pickupRequestsReceived.add(pickupRequest);
	}


	public Optional<PickupRequest> getTopPickupRequest() {
		return pickupRequestsReceived.size() > 0
				? Optional.of(pickupRequestsReceived.get(0))
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
		if (currentFloor >= MAX_FLOOR_NUMBER) {
			throw new RuntimeException(String.format("Floor cannot be bigger than %d", MAX_FLOOR_NUMBER));
		}
		log.info("Moving elevator one floor up. {}", this);
		currentFloor++;
	}


	public void floorDown() {
		if (currentFloor <= 0) {
			throw new RuntimeException("Floor cannot get negative");
		}
		log.info("Moving elevator one floor down. {}", this);
		currentFloor--;
	}


	public boolean isInMotion() {
		return direction != NONE;
	}


	public void performMove() {
		List<PickupRequest> finishedPickupRequests = getFinishedPickupRequests();
		if (!finishedPickupRequests.isEmpty()) {
			log.info("PickupRequests finished. Stop elevator and let passenger exit. elevator={}, finishedPickupRequest={}", this, finishedPickupRequests);
			changeDirection(NONE);
			pickupRequestsInProgress.removeAll(finishedPickupRequests);
			return;
		}

		if (direction == NONE) {
			PickupRequest pickupRequest = getTopPickupRequest()
				.orElseThrow(() -> new RuntimeException(String.format("Cannot move elevator. There are no pickpRequests available. elevator=%s", this)));

			log.info("Adding pickupRequest to elevator={}, pickupRequest={}", this, pickupRequest);
			pickupRequestsInProgress.add(pickupRequest);
			pickupRequestsReceived.remove(pickupRequest);

			Direction newDirection = pickupRequest.determineDirection();
			log.info("Changing direction for elevator={}. new direction={}", this, newDirection);
			changeDirection(newDirection);

			return;
		}

		if (direction == UP) {
			log.info("Moving elevator up. elevator={}", this);
			floorUp();
			return;
		}

		if (direction == DOWN) {
			log.info("Moving elevator down. elevator={}", this);
			floorDown();
			return;
		}

	}


	private List<PickupRequest> getFinishedPickupRequests() {
		return pickupRequestsInProgress.stream()
			.filter(pickupRequest -> pickupRequest.getDestinationFloor().equals(getCurrentFloor()))
			.collect(toList());
	}
}
