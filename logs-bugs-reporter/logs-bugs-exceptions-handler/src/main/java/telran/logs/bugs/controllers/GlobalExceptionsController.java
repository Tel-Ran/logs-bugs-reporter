package telran.logs.bugs.controllers;

import java.rmi.ServerException;

import javax.validation.ConstraintViolationException;

import org.slf4j.*;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import telran.logs.bugs.exceptions.DuplicatedException;
import telran.logs.bugs.exceptions.NotFoundException;

@RestControllerAdvice
public class GlobalExceptionsController {
	static Logger LOG = LoggerFactory.getLogger(GlobalExceptionsController.class);
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
String constraintViolationHandler(ConstraintViolationException e) {
	return processingExceptions(e);
	}

	@ExceptionHandler(DuplicatedException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	String duplicatedKeyHandler(DuplicatedException e) {
		return processingExceptions(e);
	}
	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String notFounHandler(NotFoundException e) {
		return processingExceptions(e);
	}
	
	

private String processingExceptions(Exception e) {
	LOG.error("exception class: {}, message: {}", e.getClass().getSimpleName(), e.getMessage());
	return e.getMessage();
}
}
