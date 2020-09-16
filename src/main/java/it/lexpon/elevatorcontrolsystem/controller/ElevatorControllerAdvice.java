package it.lexpon.elevatorcontrolsystem.controller;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import it.lexpon.elevatorcontrolsystem.datatransferobject.ErrorResponse;
import lombok.extern.slf4j.Slf4j;


@RestControllerAdvice(assignableTypes = {ElevatorController.class})
@Slf4j
public class ElevatorControllerAdvice {

	@ExceptionHandler({
		RuntimeException.class
	})
	@ResponseStatus(value = INTERNAL_SERVER_ERROR)
	public ErrorResponse handleException(RuntimeException e) {
		return buildErrorResponse(e);
	}


	@ExceptionHandler({
		IllegalStateException.class,
		HttpMessageNotReadableException.class
	})
	@ResponseStatus(value = BAD_REQUEST)
	public ErrorResponse handleException(Exception e) {
		return buildErrorResponse(e);
	}


	private ErrorResponse buildErrorResponse(Exception e) {
		log.error("An error occurred. Cause: ", e);
		return ErrorResponse.builder()
			.message(e.getMessage())
			.build();
	}

}
