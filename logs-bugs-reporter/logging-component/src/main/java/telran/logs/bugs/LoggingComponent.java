package telran.logs.bugs;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

import telran.logs.bugs.dto.LogDto;

@Component
public class LoggingComponent {
	static Logger LOG = LoggerFactory.getLogger(LoggingComponent.class);
	@Autowired
	StreamBridge streamBridge;

	@Value("${app-binding-name:logs-out-0}")
	String bindingName;

	public void sendLog(LogDto logDto) {
		streamBridge.send(bindingName, logDto);
		LOG.debug("Log was sent to:{}", bindingName);
	}
}
