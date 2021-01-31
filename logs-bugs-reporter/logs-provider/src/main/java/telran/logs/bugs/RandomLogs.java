package telran.logs.bugs;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

@Component
public class RandomLogs {
	@Value("${app-count-classes:20}")
	int nClasses;
	@Value("${app-sec-exception-prob:30}")
	int secExceptionProb;
	@Value("${app-exception-prob:10}")
	int exceptionProb;
	@Value("${app-authentication-prob:70}")
	int authenticationProb;
	@Value("${app-min-response-time:20}")
	int minResponseTime;
	@Value("${app-max-response-time:200}")
	int maxResponseTime;
public LogDto createRandomLog() {
	LogType logType = getLogType();
	return new LogDto(new Date(), logType, getArtifact(logType), getResponseTime(logType), "");
}

private int getResponseTime(LogType logType) {
	
	return logType == LogType.NO_EXCEPTION ?
			ThreadLocalRandom.current().nextInt(minResponseTime, maxResponseTime):0;
}

private  String getArtifact(LogType logType) {
	EnumMap<LogType, String> logArtifact = getLogArtifactMap();
	return logArtifact.get(logType);
}

private EnumMap<LogType, String> getLogArtifactMap() {
	EnumMap<LogType, String> res = new EnumMap<>(LogType.class);
	Arrays.asList(LogType.values()).forEach(lt -> {
		fillLogTypeArtifactMap(res, lt);
	});
	return res;
}

private void fillLogTypeArtifactMap(EnumMap<LogType, String> res, LogType lt) {
	switch(lt) {
	case AUTHENTICATION_EXCEPTION: 
		res.put(LogType.AUTHENTICATION_EXCEPTION, "authentication");
		break;
	case AUTHORIZATION_EXCEPTION:
		res.put(LogType.AUTHORIZATION_EXCEPTION, "authorization");
		break;
	case BAD_REQUEST_EXCEPTION:
		res.put(LogType.BAD_REQUEST_EXCEPTION, getRandomClassName());
		break;
	case DUPLICATED_KEY_EXCEPTION:
		res.put(LogType.DUPLICATED_KEY_EXCEPTION, getRandomClassName());
		break;
	case  NOT_FOUND_EXCEPTION:
		res.put(LogType. NOT_FOUND_EXCEPTION, getRandomClassName());
		break;
	case NO_EXCEPTION:
		res.put(LogType. NO_EXCEPTION, getRandomClassName());
		break;
	case SERVER_EXCEPTION:
		res.put(LogType. SERVER_EXCEPTION, getRandomClassName());
		break;
	
	
	}
}


private String getRandomClassName() {
	
	return "class" + ThreadLocalRandom.current().nextInt(1, nClasses + 1);
}

private LogType getLogType() {
	int chance = getChance();
	
	return chance <= exceptionProb ? getExceptionLog() : LogType.NO_EXCEPTION;
}

private LogType getExceptionLog() {
	
	return getChance() <= secExceptionProb ? getSecurityExceptionLog() : getNonSecurityExceptionLog();
}

private LogType getNonSecurityExceptionLog() {
	LogType nonSecExceptions[] = {
			LogType.BAD_REQUEST_EXCEPTION, LogType.DUPLICATED_KEY_EXCEPTION,
			LogType.NOT_FOUND_EXCEPTION, LogType.SERVER_EXCEPTION
	};
	int ind = ThreadLocalRandom.current().nextInt(0, nonSecExceptions.length);
	return nonSecExceptions[ind];
}

private LogType getSecurityExceptionLog() {
	
	return getChance() <= authenticationProb ? LogType.AUTHENTICATION_EXCEPTION : LogType.AUTHORIZATION_EXCEPTION ;
}

private int getChance() {
	
	return ThreadLocalRandom.current().nextInt(1, 101);
}
}
