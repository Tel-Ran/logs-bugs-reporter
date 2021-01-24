package telran.logs.bugs;

import java.util.function.Consumer;

import javax.validation.Valid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import telran.logs.bugs.dto.LogDto;

@SpringBootApplication
public class LogsDbPopulatorAppl {

	public static void main(String[] args) {
		SpringApplication.run(LogsDbPopulatorAppl.class, args);

	}
	@Bean
	Consumer<LogDto> getLogDtoConsumer() {
		return this::takeAndSaveLogDto;
	}
	void takeAndSaveLogDto( LogDto logDto) {
		//TODO taking and saving to MongoDB logDto
		System.out.println(logDto);
	}

}
