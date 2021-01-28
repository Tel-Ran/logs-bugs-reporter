package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
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

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.services.LogsAnalyzerService;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
public class LogsAnalyzerTest {
	static Logger LOG = LoggerFactory.getLogger(LogsAnalyzerTest.class);
	@Autowired
InputDestination producer;
	@Autowired
OutputDestination consumer;
	@Value("${app-binding-name}")
	String bindingName;
	@BeforeEach
	void setup() {
		consumer.clear();
	}
	@Test
	void analyzerTestNonException() {
		LogDto logDto = new LogDto(new Date(), LogType.NO_EXCEPTION, "artifact", 0, "result");
		producer.send(new GenericMessage<LogDto>(logDto));
		assertThrows(Exception.class, consumer::receive);
	}
	@Test
	void analyzerTestException() {
		LogDto logDto = new LogDto(new Date(), LogType.AUTHENTICATION_EXCEPTION, "artifact", 0, "result");
		producer.send(new GenericMessage<LogDto>(logDto));
		Message<byte[]> message = consumer.receive(0, bindingName);
		assertNotNull(message);
		LOG.debug("receved in consumer {}", new String(message.getPayload()));
		
	}
}
