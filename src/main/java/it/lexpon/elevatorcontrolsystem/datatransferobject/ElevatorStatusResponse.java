package it.lexpon.elevatorcontrolsystem.datatransferobject;

import java.util.List;

import it.lexpon.elevatorcontrolsystem.domainobject.Elevator;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;


@Builder
@Getter
@ToString
public class ElevatorStatusResponse {

	private final List<Elevator> elevators;

}
