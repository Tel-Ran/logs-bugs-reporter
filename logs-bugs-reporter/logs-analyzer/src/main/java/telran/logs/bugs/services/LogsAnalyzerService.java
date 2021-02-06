package telran.logs.bugs.services;

import java.util.Date;
import java.util.Set;
import java.util.function.Consumer;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

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
	@Value("${app-binding-name-exceptions:exceptions-out-0}")
	String bindingNameExceptions;
	@Value("${app-binding-name-logs:logs-out-0}")
	String bindingNameLogs;
	@Value("${app-logs-provider-artifact:logs-provider}")
	String logsProviderArtifact;
	@Autowired
StreamBridge streamBridge;
	@Autowired
	Validator validator;
	@Bean
	Consumer<LogDto> getAnalyzerBean() {
		return this::analyzerMethod;
	}
	void analyzerMethod(LogDto logDto) {
		LOG.debug("recievd log {}", logDto);
		Set<ConstraintViolation<LogDto>> violations = validator.validate(logDto);
		final LogDto logForEach = logDto;
		 if (!violations.isEmpty()) {
			violations.forEach(cv -> LOG.error("logDto : {}; field: {}; message: {}",logForEach,
					cv.getPropertyPath(), cv.getMessage()));
			logDto = new LogDto(new Date(),
					LogType.BAD_REQUEST_EXCEPTION, logsProviderArtifact, 0, violations.toString());
			
			
		 }
		 if(logDto.logType != LogType.NO_EXCEPTION) {
			 streamBridge.send(bindingNameExceptions, logDto);
				LOG.debug("log: {} sent to binding name: {}", logDto, bindingNameExceptions);
		 }
		 
		streamBridge.send(bindingNameLogs, logDto);
		
	}
}
