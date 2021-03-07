package telran.logs.bugs.client;

import java.net.URI;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class EmailProviderClient {
	static Logger LOG = LoggerFactory.getLogger(EmailProviderClient.class);
	RestTemplate restTemplate = new RestTemplate();
	@Value("${app-url-assigner-mail:xxxx}")
	String urlAssignerMail;
	
public String getEmailByArtifact(String artifact) {
	//TODO communication with sync service for programmer's email
	return "";
}
public String getAssignerMail() {
	String res;
	try {
		ResponseEntity<String> response =
				restTemplate.exchange(getUrlAssigner(), HttpMethod.GET, null, String.class);
		res = response.getBody();
	} catch (RestClientException e) {
		res = "";
	}
	LOG.debug("assigner email is {}", res);
	return res;
}
private String getUrlAssigner() {
	
	String res = urlAssignerMail + "/mail/assigner";
	LOG.debug("URL for getting assigner mail is {}", res);
	return res ;
}
}
