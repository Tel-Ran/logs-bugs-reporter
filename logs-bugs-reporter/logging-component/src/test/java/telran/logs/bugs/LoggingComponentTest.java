package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Date;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@TestPropertySource("classpath:application.properties")
@Import({TestChannelBinderConfiguration.class, LoggingComponent.class})

public class LoggingComponentTest {
@Autowired
LoggingComponent loggingComponent;
@Autowired
OutputDestination consumer;
@Value("${app-binding-name}")
String bindingName;
@Test
void componentLoading() {
	assertNotNull(loggingComponent);
	assertNotNull(consumer);
}

@Test
void sendLogTest() {

	LogDto logDto = new LogDto(new Date(), LogType.NO_EXCEPTION, "artifact", 0, "result");
	loggingComponent.sendLog(logDto);
	assertNotNull(consumer.receive(0, bindingName));
	assertNull(consumer.receive(0, bindingName));
}
}
