package telran.logs.bugs.exceptions;

@SuppressWarnings("serial")
public class DuplicatedException extends RuntimeException {
public DuplicatedException(String message) {
	super(message);
}
}
