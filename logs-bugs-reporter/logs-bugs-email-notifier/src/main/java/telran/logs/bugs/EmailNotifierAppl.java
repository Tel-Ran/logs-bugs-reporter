package telran.logs.bugs;

import java.util.function.Consumer;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import telran.logs.bugs.client.EmailProviderClient;
import telran.logs.bugs.dto.LogDto;

@SpringBootApplication
public class EmailNotifierAppl {
	private static final String PROGRAMMER_GREETING_NAME = "Programmer";
	private static final String ASSIGNER_GREETING_NAME = "Opened Bugs Assigner";
	static Logger LOG = LoggerFactory.getLogger(EmailNotifierAppl.class);
	
	@Autowired
EmailProviderClient emailClient;
	@Value("${app-subject:exception}")
	String subject;
	@Autowired
	JavaMailSender mailSender;
	public static void main(String[] args) {
		SpringApplication.run(EmailNotifierAppl.class, args);

	}
	
	@Bean
	Consumer<LogDto> getExceptionsConsumer(){
		return this::takeLogAndSendMail;
	}
	void takeLogAndSendMail(LogDto logDto) {
		String email = emailClient.getEmailByArtifact(logDto.artifact);
		String greetingName = PROGRAMMER_GREETING_NAME;
		
		if (email.isEmpty()) {
			greetingName = ASSIGNER_GREETING_NAME;
			LOG.warn("Not received email for artifact {}", logDto.artifact);
			email = emailClient.getAssignerMail();
			if(email == null || email.isEmpty()) {
				LOG.error("email 'to' has been received neither from logs-bugs-email-provider nor "
						+ "from logs-bugs-assigner-mail-provider");
				return;
			}
		}
		sendMail(logDto, email, greetingName);
		
		
	}
	private void sendMail(LogDto logDto, String email, String greetingName) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setSubject(subject);
		message.setTo(email);
		String text = getText(logDto, greetingName);
		message.setText(text);
		mailSender.send(message);
		LOG.debug("sent mail 'to' {} ; subject {}, text {}", email, subject, text);
		
	}

	private String getText(LogDto logDto, String greetingName) {
		return String.format("Hello, %s\n Exception has been received\n"
				+ "Date: %s\nException Type: %s\nArtifact: %s\nExplanation: %s",
				greetingName, logDto.dateTime, logDto.logType, logDto.artifact,
				logDto.result);
	}
	

}
