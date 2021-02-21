package telran.logs.bugs;

import java.util.*;
import java.util.stream.Collectors;

import javax.validation.constraints.NotEmpty;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import telran.logs.bugs.dto.*;
import static telran.logs.bugs.api.LogsInfoApi.*;
import telran.logs.bugs.mongo.doc.LogDoc;
import telran.logs.bugs.repo.LogRepository;

@SpringBootTest
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LogsInfoTest {
@Autowired
WebTestClient testClient;
@Autowired
LogRepository logRepository;
static List<LogDto> exceptionsAuthentication;
static List<LogDto> exceptionsAuthorization;
static List<LogDto> exceptionsBadRequest;
static List<LogDto> noExceptions;
static List<LogDto> allLogs;
static List<LogDto> allExceptions;
static Date DATE_TIME = new Date();
static final String ARTIFACT = "artifact";
private static final String AUTHENTICATION_ERROR = "Authentication error";
private static final String AUTHORIZATION_ERROR = "Authorization error";
private static final @NotEmpty String ARTIFACT_AUTHENTICATION = ARTIFACT + LogType.AUTHENTICATION_EXCEPTION;
private static final @NotEmpty String ARTIFACT_AUTHORIZATION = ARTIFACT + LogType.AUTHORIZATION_EXCEPTION;
private static final @NotEmpty String ARTIFACT_BAD_REQUEST = ARTIFACT + LogType.BAD_REQUEST_EXCEPTION;
private static final @NotEmpty String ARTIFACT_NO_EXCEPTION = ARTIFACT + LogType.NO_EXCEPTION;
@BeforeAll
static void setUpAll() {
	exceptionsAuthentication = new ArrayList<>(Arrays.asList(
			new LogDto(DATE_TIME, LogType.AUTHENTICATION_EXCEPTION,
					ARTIFACT_AUTHENTICATION, 0, AUTHENTICATION_ERROR),
			new LogDto(DATE_TIME, LogType.AUTHENTICATION_EXCEPTION,
					ARTIFACT_AUTHENTICATION, 0, AUTHENTICATION_ERROR),
			new LogDto(DATE_TIME, LogType.AUTHENTICATION_EXCEPTION,
					ARTIFACT_AUTHENTICATION, 0, AUTHENTICATION_ERROR)));
	exceptionsAuthorization = new ArrayList<>(Arrays.asList(
			new LogDto(DATE_TIME, LogType.AUTHORIZATION_EXCEPTION,
					ARTIFACT_AUTHORIZATION, 0, AUTHORIZATION_ERROR),
			new LogDto(DATE_TIME, LogType.AUTHORIZATION_EXCEPTION,
					ARTIFACT_AUTHORIZATION, 0, AUTHORIZATION_ERROR)));
	exceptionsBadRequest = new ArrayList<>(Arrays.asList(new LogDto(DATE_TIME, LogType.BAD_REQUEST_EXCEPTION,
					ARTIFACT_BAD_REQUEST, 0, "")));
			
			
			
	noExceptions = new ArrayList<>(Arrays.asList(
			new LogDto(DATE_TIME, LogType.NO_EXCEPTION, ARTIFACT_NO_EXCEPTION , 20, "result"),
			new LogDto(DATE_TIME, LogType.NO_EXCEPTION, ARTIFACT_NO_EXCEPTION, 25, "result"),
			new LogDto(DATE_TIME, LogType.NO_EXCEPTION, ARTIFACT_NO_EXCEPTION, 30, "result"),
			new LogDto(DATE_TIME, LogType.NO_EXCEPTION, ARTIFACT_NO_EXCEPTION, 35, "result")
			));
	
	fillAllExceptions();
	fillAllLogs();
	
}
private static void fillAllLogs() {
	allLogs = new ArrayList<>(noExceptions);
	allLogs.addAll(allExceptions);
}
private static void fillAllExceptions() {
	allExceptions = new ArrayList<>(exceptionsAuthentication);
	allExceptions.addAll(exceptionsAuthorization);
	allExceptions.addAll(exceptionsBadRequest);
}
@Test
@Order(1)
void allLogs() {
	setUpDbInitial();
	testClient.get().uri(LOGS).exchange().expectStatus().isOk().expectBodyList(LogDto.class)
	.isEqualTo(allLogs);
}
@Test
void allNoException() {
	testClient.get().uri(LOGS_TYPE + "?" + TYPE + "=NO_EXCEPTION").exchange().expectStatus().isOk().expectBodyList(LogDto.class)
	.isEqualTo(noExceptions);
}
@Test
void logTypesBadRequest() {
	badRequest(LOGS_TYPE, TYPE, "exception");
}
@Test
void exceptionEncounteredBadRequest() {
	badRequest(LOGS_EXCEPTION_ENCOUNTERED, AMOUNT, "ab");
}
@Test
void artifactEncounteredBadRequest() {
	badRequest(LOGS_ARTIFACT_ENCOUNTERED, AMOUNT, "ab");
}
private void badRequest(String baseUrl, String parameter, String value) {
	testClient.get().uri(baseUrl + "?" + parameter + "=" + value).exchange().expectStatus().isBadRequest();
}
@Test
void allException() {
	testClient.get().uri(LOGS_EXCEPTIONS).exchange().expectStatus().isOk().expectBodyList(LogDto.class)
	.isEqualTo(allExceptions);
}
private void setUpDbInitial() {
	Flux<LogDoc> savingFlux =
			logRepository.saveAll(allLogs.stream().map(LogDoc::new).collect(Collectors.toList()));
	savingFlux.buffer().blockFirst();
	
	
}
@Test
void logTypeCount() {
	LogTypeCount[] expected = {
		new LogTypeCount(LogType.NO_EXCEPTION, noExceptions.size()),
		new LogTypeCount(LogType.AUTHENTICATION_EXCEPTION, exceptionsAuthentication.size()),
		new LogTypeCount(LogType.AUTHORIZATION_EXCEPTION, exceptionsAuthorization.size()),
		new LogTypeCount(LogType.BAD_REQUEST_EXCEPTION, exceptionsBadRequest.size())
	};
	runTest(expected, LOGS_DISTRIBUTION_TYPE, LogTypeCount[].class);
}
@Test
void artifactCount() {
	ArtifactCount[] expected = {
			new ArtifactCount(ARTIFACT_NO_EXCEPTION, noExceptions.size()),
			new ArtifactCount(ARTIFACT_AUTHENTICATION, exceptionsAuthentication.size()),
			new ArtifactCount(ARTIFACT_AUTHORIZATION, exceptionsAuthorization.size()),
			new ArtifactCount(ARTIFACT_BAD_REQUEST, exceptionsBadRequest.size())
	};
	runTest(expected, LOGS_DISTRIBUTION_ARTIFACT, ArtifactCount[].class);
}
@Test
void artifactMostEncountered() {
	String [] expected = {
			ARTIFACT_NO_EXCEPTION, 	ARTIFACT_AUTHENTICATION
	};
	runTest(expected, LOGS_ARTIFACT_ENCOUNTERED, String[].class);
}
@Test
void exceptionMostEncountered() {
	LogType[] expected = {
			LogType.AUTHENTICATION_EXCEPTION, LogType.AUTHORIZATION_EXCEPTION
	};
	runTest(expected, LOGS_EXCEPTION_ENCOUNTERED,LogType[].class);
}
private <T> void runTest(T[] expected, String uriString, Class<T[]> clazz) {
	testClient.get().uri(uriString).exchange().expectStatus().isOk()
	.expectBody(clazz).isEqualTo(expected);
}


}
