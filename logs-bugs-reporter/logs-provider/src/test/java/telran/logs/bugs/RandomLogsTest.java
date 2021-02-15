package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import telran.logs.bugs.dto.*;
import telran.logs.bugs.random.RandomLogs;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
public class RandomLogsTest {
	static Logger LOG = LoggerFactory.getLogger(RandomLogsTest.class);
	
	private static final int N_LOGS_SENT = 10;
	@Autowired
	RandomLogs randomLogs;
	@Autowired
	 OutputDestination output;
	
	@Test
	void sendRandomLogs() throws InterruptedException, JsonMappingException, JsonProcessingException {
		HashSet<String> set = new HashSet<>();
		for (int i = 0; i < N_LOGS_SENT; i++) {
			Message<byte[]> receivedMessage = null;
			while (receivedMessage == null) {
				receivedMessage = output.receive();
			}
			byte[] messageBytes = receivedMessage.getPayload();
			String messageStr = new String(messageBytes);
			set.add(messageStr);
			LOG.debug("received in test: {}", messageStr);
			
		}
		assertEquals(N_LOGS_SENT, set.size());
	}

}
