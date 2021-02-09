package telran.logs.bugs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient
public class AssignerMailProviderTest {
	@Autowired
WebTestClient client;
	@Value("${app-assigner-mail}")
	String assignerMail;
	@Test
	void assignerMailTest() {
		client.get().uri("/mail/assigner").exchange().expectStatus().isOk()
		.expectBody(String.class).isEqualTo(assignerMail);
	}
}
