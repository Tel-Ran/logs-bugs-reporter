package telran.logs.bugs;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.Errors;
//import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.mongo.doc.LogDoc;
import telran.logs.bugs.mongo.repo.LogsRepo;

@SpringBootApplication
public class LogsDbPopulatorAppl {

	public static void main(String[] args) {
		SpringApplication.run(LogsDbPopulatorAppl.class, args);

	}
	@Bean
	
	Consumer<LogDto> getLogDtoConsumer() {
		return this::takeAndSaveLogDto;
	}
	@Autowired
	Validator validator;
	@Autowired
	LogsRepo logsRepository;
	void takeAndSaveLogDto(LogDto logDto) {
		//TODO taking and saving to MongoDB logDto
		
		 Set<ConstraintViolation<LogDto>> violations = validator.validate(logDto);
		 if (!violations.isEmpty()) {
			violations.forEach(cv -> System.out.println(cv.getMessage()));
		 } else {
			 logsRepository.save(new LogDoc(logDto));
		 }
		System.out.println(logDto);
	}

}
