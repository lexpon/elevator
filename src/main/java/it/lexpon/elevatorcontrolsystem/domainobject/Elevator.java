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

	public static final int MAX_FLOOR_NUMBER = 10;

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
		pickupRequestsOpen.add(pickupRequest);
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


	public boolean performOneTimeStep() {
		if (handleFinishedPickupRequests())
			return true;
		if (handlePickupRequestsInProgress())
			return true;
		if (handlePickupRequestsOpen())
			return true;
		if (handleDirectionUp())
			return true;
		return handleDirectionDown();
	}


	private boolean handleDirectionDown() {
		if (direction == DOWN) {
			log.info("Moving elevator down. elevator={}", this);
			floorDown();
			handleSimilarPickupRequests();
			return true;
		}
		return false;
	}


	private boolean handleDirectionUp() {
		if (direction == UP) {
			log.info("Moving elevator up. elevator={}", this);
			floorUp();
			handleSimilarPickupRequests();
			return true;
		}
		return false;
	}


	private void handleSimilarPickupRequests() {
		List<PickupRequest> pickupRequests = otherPickupRequestsAtCurrentFloorWithSameDirection();
		if (!pickupRequests.isEmpty()) {
			log.info("There are other pickupRequests with current floor and same direction. Taking them as well. elevator={}, pickupRequests={}", this,
				pickupRequests);
			pickupRequestsInProgress.addAll(pickupRequests);
			pickupRequestsOpen.removeAll(pickupRequests);
		}
	}


	private boolean handlePickupRequestsOpen() {
		if (direction == NONE && pickupRequestsOpen.size() > 0) {
			PickupRequest pickupRequest = pickupRequestsOpen.stream()
				.findFirst()
				.orElseThrow(() -> new RuntimeException(String.format("Cannot move elevator. There are no pickpRequests available. elevator=%s", this)));

			log.info("Adding pickupRequest to elevator={}, pickupRequest={}", this, pickupRequest);
			pickupRequestsInProgress.add(pickupRequest);
			pickupRequestsOpen.remove(pickupRequest);

			Direction newDirection = pickupRequest.determineDirection();
			changeDirection(newDirection);

			return true;
		}
		return false;
	}


	private boolean handlePickupRequestsInProgress() {
		if (direction == NONE && !pickupRequestsInProgress.isEmpty()) {
			Direction newDirection = pickupRequestsInProgress.get(0).determineDirection();
			log.info("Elevator was standing still, but there are still pickupRequests in progress. Start moving elevator again. elevator={}, newDirection={}",
				this, newDirection);
			changeDirection(newDirection);
			return true;
		}
		return false;
	}


	private boolean handleFinishedPickupRequests() {
		List<PickupRequest> finishedPickupRequests = getFinishedPickupRequests();
		if (!finishedPickupRequests.isEmpty()) {
			log.info("PickupRequests finished. Stop elevator and let passenger exit. elevator={}, finishedPickupRequest={}", this, finishedPickupRequests);
			changeDirection(NONE);
			pickupRequestsInProgress.removeAll(finishedPickupRequests);
			return true;
		}
		return false;
	}


	private List<PickupRequest> getFinishedPickupRequests() {
		return pickupRequestsInProgress.stream()
			.filter(pickupRequest -> pickupRequest.getDestinationFloor().equals(getCurrentFloor()))
			.collect(toList());
	}


	private List<PickupRequest> otherPickupRequestsAtCurrentFloorWithSameDirection() {
		return pickupRequestsOpen.stream()
			.filter(pickupRequest -> pickupRequest.getCurrentFloor().equals(getCurrentFloor()))
			.filter(pickupRequest -> pickupRequest.determineDirection().equals(getDirection()))
			.collect(toList());
	}
}
