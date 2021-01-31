package telran.logs.bugs;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.mongo.doc.LogDoc;
import telran.logs.bugs.mongo.repo.LogsRepo;

@SpringBootApplication
public class LogsDbPopulatorAppl {
static Logger LOG = LoggerFactory.getLogger(LogsDbPopulatorAppl.class);
	public static void main(String[] args) {
		SpringApplication.run(LogsDbPopulatorAppl.class, args);

	}
	@Value("${app-binding-name:exceptions-out-0}")
	String bindingName;
	@Autowired
	StreamBridge streamBridge;
	@Bean
	
	Consumer<LogDto> getLogDtoConsumer() {
		return this::takeAndSaveLogDto;
	}
	@Autowired
	Validator validator;
	@Autowired
	LogsRepo logsRepository;
	void takeAndSaveLogDto(LogDto logDto) {
		// taking and saving to MongoDB logDto
		LOG.debug("received log: {}", logDto);
		 Set<ConstraintViolation<LogDto>> violations = validator.validate(logDto);
		 if (!violations.isEmpty()) {
			violations.forEach(cv -> LOG.error("logDto : {}; field: {}; message: {}",logDto,
					cv.getPropertyPath(), cv.getMessage()));
			LogDto exceptionLog = new LogDto(new Date(),
					LogType.BAD_REQUEST_EXCEPTION, LogsDbPopulatorAppl.class.getName(), 0, violations.toString());
			logsRepository.save(new LogDoc(exceptionLog));
			LOG.debug("log: {} saved to Mongo collection", exceptionLog);
			streamBridge.send(bindingName, exceptionLog);
			LOG.debug("log: {} sent to binding name: {}", exceptionLog, bindingName);
		 } else {
			 logsRepository.save(new LogDoc(logDto));
			 LOG.debug("log: {} saved to Mongo collection", logDto);
		 }
		
	}

}
