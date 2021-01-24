package telran.logs.bugs;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.GenericMessage;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
public class LogsDbPopulatorTest {
	@Autowired
InputDestination input;
	@Test
	void takeLogDto() {
		input.send(new GenericMessage<LogDto>
		(new LogDto(new Date(), LogType.NO_EXCEPTION, "artifact", 20, "result")));
		//TODO testing of saving LogDto into MongoDB
		
	}
}
