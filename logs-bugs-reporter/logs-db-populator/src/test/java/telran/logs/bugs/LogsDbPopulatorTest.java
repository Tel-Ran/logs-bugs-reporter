package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.GenericMessage;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.mongo.repo.LogsRepo;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
public class LogsDbPopulatorTest {
	@Autowired
InputDestination input;
	@Autowired
	LogsRepo logsRepo;
	@BeforeEach
	void setUp() {
		logsRepo.deleteAll();
	}
	
	@Test
	void takeLogDtoAndSaveNormal() {
		LogDto logDto = new LogDto(new Date(), LogType.NO_EXCEPTION, "artifact", 0, "");
		sendLog(logDto);
		assertEquals(logDto, logsRepo.findAll().get(0).getLogDto());
	}
	@Test
	void takeLogDtoAndNoSaveNoDate() {
		LogDto logDto = new LogDto(null, LogType.NO_EXCEPTION, "artifact", 0, "");
		sendLog(logDto);
		assertTrue(logsRepo.findAll().isEmpty());
	}
	@Test
	void takeLogDtoAndNoSaveNoLogType() {
		LogDto logDto = new LogDto(new Date(), null, "artifact", 0, "");
		sendLog(logDto);
		assertTrue(logsRepo.findAll().isEmpty());
	}
	@Test
	void takeLogDtoAndNoSaveNoArtifact() {
		LogDto logDto = new LogDto(new Date(), LogType.NO_EXCEPTION, "", 0, "");
		sendLog(logDto);
		assertTrue(logsRepo.findAll().isEmpty());
	}
	
	private void sendLog(LogDto logDto) {
		input.send(new GenericMessage<LogDto>
		(logDto));
	}
}
