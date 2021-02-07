package telran.logs.bugs;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.*;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.*;
import org.springframework.context.annotation.*;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
public class LogsAnalyzerTest {
	static Logger LOG = LoggerFactory.getLogger(LogsAnalyzerTest.class);
	@Autowired
InputDestination producer;
	@Autowired
OutputDestination consumer;
	@Value("${app-binding-name-exceptions:exceptions-out-0}")
	String bindingNameExceptions;
	@Value("${app-binding-name-exceptions:logs-out-0}")
	String bindingNameLogs;
	@Value("${app-logs-provider-artifact:logs-provider}")
	String logsProviderArtifact;
	@Test
	void analyzerTestNonException() {
		/* logDto is valid and no exception */
		LogDto logDto = new LogDto(new Date(), LogType.NO_EXCEPTION, "artifact", 0, "result");
		sendLog(logDto);
		assertNotNull(consumer.receive(0, bindingNameLogs));
		assertNull(consumer.receive(0, bindingNameExceptions));
	}
	@Test
	void analyzerTestException() throws JsonParseException, JsonMappingException, IOException {
		/* logDto is valid and exception */
		LogDto logDto = new LogDto(new Date(), LogType.AUTHENTICATION_EXCEPTION, "artifact", 0, "result");
		sendLog(logDto);
		Message<byte[]> message = consumer.receive(0, bindingNameExceptions);
		assertNotNull(message);
		message = consumer.receive(0, bindingNameLogs);
		assertNotNull(message);
		LOG.debug("receved in consumer {}", new String(message.getPayload()));
		
	}
	@Test
	void analyserTestNoDate() {
		/* logDTo is invalid, no date */
		LogDto logDto = new LogDto(null, LogType.NO_EXCEPTION, "artifact", 0, "");
		sendLog(logDto);
		testWrongLogDto();
	}

	private void testWrongLogDto() {
		Message<byte[]> message = consumer.receive(0, bindingNameExceptions);
		
		String messageStr = new String(message.getPayload());
		assertTrue(messageStr.contains(LogType.BAD_REQUEST_EXCEPTION.toString()));
		assertTrue(messageStr.contains(logsProviderArtifact));
		
		
		
		
	}

	@Test
	void analyserTestNoLogType() {
		LogDto logDto = new LogDto(new Date(), null, "artifact", 0, "");
		sendLog(logDto);
		testWrongLogDto();
	}

	@Test
	void analyserTestNoArtifact() {
		LogDto logDto = new LogDto(new Date(), LogType.NO_EXCEPTION, "", 0, "");
		sendLog(logDto);
		testWrongLogDto();
	}
private void sendLog(LogDto logDto) {
		
		producer.send(new GenericMessage<LogDto>(logDto));
	}

}
