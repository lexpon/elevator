package it.lexpon.elevatorcontrolsystem.domainobject;

import static it.lexpon.elevatorcontrolsystem.domainobject.Direction.*;
import static it.lexpon.elevatorcontrolsystem.domainobject.Direction.NONE;
import static java.util.stream.Collectors.*;
import static lombok.AccessLevel.*;

import java.util.ArrayList;
import java.util.List;

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

	public final static int MAX_FLOOR_NUMBER = 10;
	private final static int MAX_OPEN_PICKUP_REQUESTS = 10;

	private final Integer id;
	private Integer currentFloor;
	private Direction direction;
	private final List<PickupRequest> pickupRequestsOpen;
	private final List<PickupRequest> pickupRequestsInProgress;

	public static Elevator create(Integer id) {
		return Elevator.builder()
			.id(id)
			.currentFloor(0)
			.direction(NONE)
			.pickupRequestsOpen(new ArrayList<>())
			.pickupRequestsInProgress(new ArrayList<>())
			.build();
	}


	public void addRequest(PickupRequest pickupRequest) {
		if (pickupRequestsOpen.size() >= MAX_OPEN_PICKUP_REQUESTS) {
			throw new IllegalStateException(String.format("Too many pickupRequests for elevator. Can handle maximum %d requests.", MAX_OPEN_PICKUP_REQUESTS));
		}
		pickupRequestsOpen.add(pickupRequest);
	}


	private boolean hasRequestsInProgress() {
		return pickupRequestsInProgress.size() > 0;
	}


	private boolean hasRequestsOpen() {
		return pickupRequestsOpen.size() > 0;
	}


	public PickupRequestRating ratePickupRequest(PickupRequest pickupRequest) {
		return PickupRequestRating.builder()
			.floorDistance(Math.abs(currentFloor - pickupRequest.getCurrentFloor()))
			.sameDirection(direction == pickupRequest.determineDirection())
			.elevatorStanding(direction == NONE)
			.numberOfPickupRequestsOpen(pickupRequestsOpen.size())
			.numberOfPickupRequestsInProgress(pickupRequestsInProgress.size())
			.build();
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


	public void performOneTimeStep() {

		switch (direction) {
			case NONE:
				// elevator standing still
				checkForOpenRequests();
				break;
			case NONE_CONTINUE_UP:
				// elevator standing to let passenger enter or exit. continue up afterwards
				changeDirection(UP);
				moveUpOrDown();
				break;
			case NONE_CONTINUE_DOWN:
				// elevator standing to let passenger enter or exit. continue down afterwards
				changeDirection(DOWN);
				moveUpOrDown();
				break;
			case UP:
			case DOWN:
				// elevator moving up or down
				List<PickupRequest> reachedRequests = reachedRequests();
				List<PickupRequest> similarRequests = similarRequests();
				if (reachedRequests.size() > 0) {
					log.info("Floor reached for requests. elevator={}, requests={}", this, reachedRequests);
					pickupRequestsInProgress.removeAll(reachedRequests);
				}
				if (similarRequests.size() > 0) {
					log.info("There are similar requests. elevator={}, requests={}", this, similarRequests);
					pickupRequestsInProgress.addAll(similarRequests);
					pickupRequestsOpen.removeAll(similarRequests);
				}
				if (reachedRequests.size() > 0 || similarRequests.size() > 0) {
					stopElevator();
				}
				else {
					moveUpOrDown();
				}

				break;
		}

	}


	private void checkForOpenRequests() {
		if (hasRequestsOpen()) {
			PickupRequest request = pickupRequestsOpen.get(0);
			if (request.getCurrentFloor().equals(currentFloor)) {
				log.info("Elevator is at the same floor as the request. Let passenger enter. elevator={}, request={}", this, request);
				pickupRequestsInProgress.add(request);
				pickupRequestsOpen.remove(request);
				changeDirection(request.determineDirection());
			}
			else if (currentFloor < request.getCurrentFloor()) {
				log.info("Elevator is below the requested floor. changing direction to move up. elevator={}, request={}", this, request);
				changeDirection(UP);
			}
			else if (currentFloor > request.getCurrentFloor()) {
				log.info("Elevator is above the requested floor. changing direction to move up. elevator={}, request={}", this, request);
				changeDirection(DOWN);
			}
		}
	}


	private void moveUpOrDown() {
		switch (direction) {
			case UP:
			case NONE_CONTINUE_UP:
				floorUp();
				break;
			case DOWN:
			case NONE_CONTINUE_DOWN:
				floorDown();
				break;
		}
	}


	private List<PickupRequest> reachedRequests() {
		return pickupRequestsInProgress.stream()
			.filter(req -> currentFloor.equals(req.getDestinationFloor()))
			.collect(toList());
	}


	private List<PickupRequest> similarRequests() {
		return pickupRequestsOpen.stream()
			.filter(req -> {
				boolean sameFloor = currentFloor.equals(req.getCurrentFloor());
				boolean sameDirection = direction == req.determineDirection();
				if (hasRequestsInProgress()) {
					return sameFloor && sameDirection;
				}
				return sameFloor;
			})
			.collect(toList());
	}


	private void stopElevator() {
		// not all requests done. keep direction after stopping elevator.
		if (hasRequestsInProgress()) {
			switch (pickupRequestsInProgress.get(0).determineDirection()) {
				case UP:
					changeDirection(NONE_CONTINUE_UP);
					break;
				case DOWN:
					changeDirection(NONE_CONTINUE_DOWN);
					break;
			}
		}

		// all requests done. stop elevator completely.
		else {
			changeDirection(NONE);
		}
	}

}
