package it.lexpon.elevatorcontrolsystem.service;

import static it.lexpon.elevatorcontrolsystem.domainobject.Direction.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import it.lexpon.elevatorcontrolsystem.datatransferobject.ElevatorStatusResponse;
import it.lexpon.elevatorcontrolsystem.domainobject.Elevator;


public class ElevatorServiceTest {

	private final List<Integer> elevatorIds = List.of(1);

	private final ElevatorService testee = new ElevatorService(elevatorIds);

	@Test
	public void shouldGetElevatorStatus() {
		// GIVEN

		// WHEN
		ElevatorStatusResponse response = testee.getStatus();

		// THEN
		List<Elevator> elevators = response.getElevators();
		assertThat(elevators.size()).isEqualTo(1);

		Elevator expectedElevator = Elevator.create(1);
		expectedElevator.changeDirection(NONE);
		expectedElevator.changeFloor(0);
		assertThat(elevators.get(0)).isEqualToComparingFieldByField(expectedElevator);
	}

}
