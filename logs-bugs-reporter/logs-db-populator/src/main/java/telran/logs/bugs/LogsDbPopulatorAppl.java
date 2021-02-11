package telran.logs.bugs;

import java.util.function.Consumer;


import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.mongo.doc.LogDoc;
import telran.logs.bugs.mongo.repo.LogsRepo;

@SpringBootApplication
public class LogsDbPopulatorAppl {
	static Logger LOG = LoggerFactory.getLogger(LogsDbPopulatorAppl.class);

	public static void main(String[] args) {
		SpringApplication.run(LogsDbPopulatorAppl.class, args);

	}

	
	

	@Bean

	Consumer<LogDto> getLogDtoConsumer() {
		return this::takeAndSaveLogDto;
	}

	@Autowired
	LogsRepo logsRepository;

	void takeAndSaveLogDto(LogDto logDto) {
		// taking and saving to MongoDB logDto
		LOG.debug("received log: {}", logDto);

		logsRepository.save(new LogDoc(logDto)).subscribe(log -> LOG.debug("log: {} saved to Mongo collection", log.getLogDto()));
		LOG.debug("start saving log and finishing of takeAndSaveLogDto method");

	}

}
