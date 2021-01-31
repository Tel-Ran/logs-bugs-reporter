package telran.logs.bugs;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.*;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.mongo.doc.LogDoc;
import telran.logs.bugs.mongo.repo.LogsRepo;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
public class LogsDbPopulatorTest {
	static Logger LOG = LoggerFactory.getLogger(LogsDbPopulatorTest.class);
	@Value("${app-binding-name:exceptions-out-0}")
	String bindingName;
	@Autowired
	InputDestination input;
	@Autowired
	OutputDestination output;
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
		List<LogDoc> logDocs = logsRepo.findAll();
		assertEquals(1, logDocs.size());
		assertEquals(logDto, logDocs.get(0).getLogDto());
	}

	@Test
	void takeLogDtoAndNoSaveNoDate() {
		LogDto logDto = new LogDto(null, LogType.NO_EXCEPTION, "artifact", 0, "");
		sendLog(logDto);
		testWrongLogDto();
	}

	private void testWrongLogDto() {
		List<LogDoc> logDocs = logsRepo.findAll();
		assertEquals(1, logDocs.size());
		LogDto logDto = logDocs.get(0).getLogDto();
		assertEquals(LogType.BAD_REQUEST_EXCEPTION, logDto.logType);
		assertEquals(LogsDbPopulatorAppl.class.getCanonicalName(), logDto.artifact);
		assertEquals(0, logDto.responseTime);
		assertFalse(logDto.result.isEmpty());
		try {
			String exceptionLogStr = new String(output.receive(0, bindingName).getPayload());
			LOG.debug("exception log: {} sent to binding name: {} ", exceptionLogStr, bindingName);
		} catch (Exception e) {
			fail("No sent exception log to binding name: " + bindingName);
		}
		
		
	}

	@Test
	void takeLogDtoAndNoSaveNoLogType() {
		LogDto logDto = new LogDto(new Date(), null, "artifact", 0, "");
		sendLog(logDto);
		testWrongLogDto();
	}

	@Test
	void takeLogDtoAndNoSaveNoArtifact() {
		LogDto logDto = new LogDto(new Date(), LogType.NO_EXCEPTION, "", 0, "");
		sendLog(logDto);
		testWrongLogDto();
	}

	private void sendLog(LogDto logDto) {
		
		input.send(new GenericMessage<LogDto>(logDto));
	}
}
