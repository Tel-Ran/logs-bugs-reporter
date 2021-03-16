package telran.logs.bugs;



import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import telran.logs.bugs.random.*;
import telran.logs.bugs.dto.LogDto;
import java.util.function.Supplier;

@SpringBootApplication
public class RandomLogsAppl {
	private static final long TIME_OUT = 100000;
	static Logger LOG = LoggerFactory.getLogger(RandomLogsAppl.class);
@Autowired
RandomLogs randomLogs;
	public static void main(String[] args) throws InterruptedException {
		ConfigurableApplicationContext ctx = SpringApplication.run(RandomLogsAppl.class, args);
		Thread.sleep(TIME_OUT);
		ctx.close();

	}
	@Bean
Supplier<LogDto>  random_logs_provider() {
	return this::sendRandomLog;
}
	LogDto sendRandomLog() {
		LogDto logDto = randomLogs.createRandomLog();
		LOG.debug("sent log: {}", logDto);
		return logDto;
	}

}
