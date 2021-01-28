package telran.logs.bugs.services;

import java.util.function.Consumer;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

@Service
public class LogsAnalyzerService {
	static Logger LOG = LoggerFactory.getLogger(LogsAnalyzerService.class);
	@Value("${app-binding-name:exceptions-out-0}")
	String bindingName;
	@Autowired
StreamBridge streamBridge;
	@Bean
	Consumer<LogDto> getAnalyzerBean() {
		return this::analyzerMethod;
	}
	void analyzerMethod(LogDto logDto) {
		LOG.debug("recievd log {}", logDto);
		if (logDto.logType != null && logDto.logType != LogType.NO_EXCEPTION) {
			streamBridge.send(bindingName, logDto);
		}
	}
}
