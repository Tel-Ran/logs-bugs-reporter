package telran.logs.bugs.aop;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import javax.validation.ConstraintViolationException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import telran.logs.bugs.LoggingComponent;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.exceptions.DuplicatedException;
import telran.logs.bugs.exceptions.NotFoundException;


@Component
@Aspect
public class LoggerAspect {
	static Logger LOG = LoggerFactory.getLogger(LoggerAspect.class);
	@Value("${app-result-length:200}")
	int resultLength;
	@Autowired
	LoggingComponent loggingComponent;
	@Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
	void pointCutRestController() {}
	@Around("pointCutRestController()")
	Object aroundLoggingAdvice(ProceedingJoinPoint jp) throws Throwable {
		String artifact = jp.getTarget().getClass().getName();
		Instant start = Instant.now();
		try {
			Object result = jp.proceed();
			int responseTime = (int)ChronoUnit.MILLIS.between(start, Instant.now());
			noExceptionLog(artifact, responseTime, result.toString());
			return result;
			
		}catch (Throwable e) {
			exceptionLog(artifact, e);
			throw(e);
		}
		
	}
	private void exceptionLog(String artifact, Throwable e) {
		LogType logType;
		logType = getLogType(e);
		LogDto logDto = new LogDto(new Date(), logType, artifact, 0, e.getMessage());
		loggingComponent.sendLog(logDto);
		LOG.debug("Exception log: {} was sent to stream binder", logDto);
		
	}
	private LogType getLogType(Throwable e) {
		LogType logType;
		Class<?> clazz = e.getClass();
		if(clazz == IllegalArgumentException.class || clazz
				== ConstraintViolationException.class) {
			logType = LogType.BAD_REQUEST_EXCEPTION;
		} else if (clazz == DuplicatedException.class) {
			logType = LogType.DUPLICATED_KEY_EXCEPTION;
		} else if (clazz == NotFoundException.class) {
			logType = LogType.NOT_FOUND_EXCEPTION;
		} else {
			logType = LogType.SERVER_EXCEPTION;
		}
		return logType;
	}
	private void noExceptionLog(String artifact, int responseTime, String result ) {
		if (result.length() > resultLength) {
			result = result.substring(0, resultLength);
		}
		LogDto logDto = new LogDto(new Date(), LogType.NO_EXCEPTION,
				artifact, responseTime, result);
		loggingComponent.sendLog(logDto);
		LOG.debug("log: {} was sent to stream binder", logDto);
	}
	
}
