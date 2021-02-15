package telran.logs.bugs;

import java.util.*;
import java.util.stream.Collectors;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
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
static List<LogDto> exceptions;
static List<LogDto> noExceptions;
static List<LogDto> allLogs;
static Date DATE_TIME = new Date();
static final String ARTIFACT = "artifact";
private static final String AUTHENTICATION_ERROR = "Authentication error";
private static final String AUTHORIZATION_ERROR = "Authorization error";
@BeforeAll
static void setUpAll() {
	exceptions = new ArrayList<>(Arrays.asList(
			new LogDto(DATE_TIME, LogType.AUTHENTICATION_EXCEPTION,
					ARTIFACT, 0, AUTHENTICATION_ERROR),
			new LogDto(DATE_TIME, LogType.AUTHORIZATION_EXCEPTION,
					ARTIFACT, 0, AUTHORIZATION_ERROR),
			new LogDto(DATE_TIME, LogType.BAD_REQUEST_EXCEPTION,
					ARTIFACT, 0, "")
			
			
			));
	noExceptions = new ArrayList<>(Arrays.asList(
			new LogDto(DATE_TIME, LogType.NO_EXCEPTION, ARTIFACT, 20, "result"),
			new LogDto(DATE_TIME, LogType.NO_EXCEPTION, ARTIFACT, 25, "result"),
			new LogDto(DATE_TIME, LogType.NO_EXCEPTION, ARTIFACT, 30, "result")
			));
	allLogs = new ArrayList<>(noExceptions);
	allLogs.addAll(exceptions);
	
}
@Test
@Order(1)
void allLogs() {
	setUpDbInitial();
	testClient.get().uri("/logs").exchange().expectStatus().isOk().expectBodyList(LogDto.class)
	.isEqualTo(allLogs);
}
@Test
void allNoException() {
	testClient.get().uri("/logs/type?type=NO_EXCEPTION").exchange().expectStatus().isOk().expectBodyList(LogDto.class)
	.isEqualTo(noExceptions);
}
@Test
void badRequest() {
	testClient.get().uri("/logs/type?type=EXCEPTION").exchange().expectStatus().isBadRequest();
}
@Test
void allException() {
	testClient.get().uri("/logs/exceptions").exchange().expectStatus().isOk().expectBodyList(LogDto.class)
	.isEqualTo(exceptions);
}
private void setUpDbInitial() {
	Flux<LogDoc> savingFlux =
			logRepository.saveAll(allLogs.stream().map(LogDoc::new).collect(Collectors.toList()));
	savingFlux.buffer().blockFirst();
	
	
}


}
