package it.lexpon.elevatorcontrolsystem.datatransferobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Builder
@AllArgsConstructor
@Setter
@Getter
@ToString
public class ErrorResponse {

	private final String message;

}
