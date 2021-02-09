package telran.logs.bugs;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class AssignerMailProviderAppl {
	static Logger LOG = LoggerFactory.getLogger(AssignerMailProviderAppl.class);
	@Value("${app-assigner-mail:logs.bugs.reporter+assigner@gmail.com}")
String assignerMail;
	public static void main(String[] args) {
		SpringApplication.run(AssignerMailProviderAppl.class, args);

	}
	@GetMapping("/mail/assigner")
	String getAssignerMail() {
		LOG.debug("assigner mail is {}", assignerMail);
		return assignerMail;
	}
	

}
