package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import telran.logs.bugs.RandomLogs;
import telran.logs.bugs.dto.*;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
public class RandomLogsTest {
	private static final String AUTHENTICATION_ARTIFACT = "authentication";
	private static final String AUTHORIZATION_ARTIFACT = "authorization";
	private static final String CLASS_ARTIFACT = "class";
	private static final long N_LOGS = 100000;
	@Autowired
	RandomLogs randomLogs;
	@Autowired
	 OutputDestination output;
	@Test
	void logTypeArtifactTest() throws Exception {
		
		EnumMap<LogType, String> logTypeArtifactsMap = getMapForTest();
		logTypeArtifactsMap.forEach((k, v) -> {
			switch (k) {
			case AUTHENTICATION_EXCEPTION:
				assertEquals(AUTHENTICATION_ARTIFACT, v);
				break;
			case AUTHORIZATION_EXCEPTION:
				assertEquals(AUTHORIZATION_ARTIFACT, v);
				break;
			default:
				assertEquals(CLASS_ARTIFACT, v);
			
			}
		});
	}

	private EnumMap<LogType, String> getMapForTest()
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Method getMapMethod = randomLogs.getClass()
				.getDeclaredMethod("getLogArtifactMap");
		getMapMethod.setAccessible(true);
		@SuppressWarnings("unchecked")
		EnumMap<LogType, String> logTypeArtifactsMap =
				(EnumMap<LogType, String>) getMapMethod.invoke(randomLogs);
		return logTypeArtifactsMap;
	}
	@Test
	void generation() throws Exception{
		
		List<LogDto> logs =Stream
				.generate(() -> randomLogs.createRandomLog()).limit(N_LOGS)
				.collect(Collectors.toList());
		testLogContent(logs);
		Map<LogType, Long> logTypeOccurrences = 
				logs.stream().collect(Collectors.groupingBy(l -> l.logType, Collectors.counting()));
		logTypeOccurrences.forEach((k, v) -> {
			System.out.println();
		});
		
	}

	private void testLogContent(List<LogDto> logs) {
		logs.forEach(log -> {
			switch (log.logType) {
			case AUTHENTICATION_EXCEPTION:
				assertEquals(AUTHENTICATION_ARTIFACT, log.artifact);
				assertEquals(0, log.responseTime);
				assertTrue(log.result.isEmpty());
				break;
			case AUTHORIZATION_EXCEPTION:
				assertEquals(AUTHORIZATION_ARTIFACT, log.artifact);
				assertEquals(0, log.responseTime);
				assertTrue(log.result.isEmpty());
				break;
			
			case NO_EXCEPTION:
				assertEquals(CLASS_ARTIFACT, log.artifact);
				assertTrue(log.responseTime > 0);
				assertTrue(log.result.isEmpty());
				break;
			
			default:
				assertEquals(CLASS_ARTIFACT, log.artifact);
				assertEquals(0, log.responseTime);
				assertTrue(log.result.isEmpty());
				break;
			
			}
		});
	}
	@Test
	void sendRandomLogs() throws InterruptedException {
		for (int i = 0; i < 10; i++) {
			byte[] messageBytes = output.receive().getPayload();
			String messageStr = new String(messageBytes);
			System.out.println(messageStr);
			Thread.sleep(1500);
		}
	}

}
