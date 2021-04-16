package telran.logs.bugs.client;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import telran.logs.bugs.discovery.LoadBalancer;

@Component
public class EmailProviderClient {
	static Logger LOG = LoggerFactory.getLogger(EmailProviderClient.class);
	@Autowired
	LoadBalancer loadBalancer;
	RestTemplate restTemplate = new RestTemplate();
	@Value("${app-assigner-mail-provider:assigner-mail-provider}")
	String assignerServiceName;
	@Value("${app-email-provider:email-provider}")
	String programmerServiceName;
public String getEmailByArtifact(String artifact) {
	String urlMailProvider = getUrlMailArtifact(artifact);
	String res = getMail(urlMailProvider);
	LOG.debug("Programmer mail is {}", res);
	return res;
}
private String getUrlMailArtifact(String artifact) {
	String res = loadBalancer.getBaseUrl(programmerServiceName) + "/email/" + artifact;
	LOG.debug("url for getting email by artifact is {}", res);
	return res ;
}
public String getAssignerMail() {
	String res;
	String urlMailProvider = getUrlAssigner();
	res = getMail(urlMailProvider);
	LOG.debug("assigner email is {}", res);
	return res;
}
private String getMail(String urlMailProvider) {
	String res;
	try {
		ResponseEntity<String> response =
				restTemplate.exchange(urlMailProvider, HttpMethod.GET, null, String.class);
		res = response.getBody();
	} catch (RestClientException e) {
		LOG.error("request to url {} thrown exception {}", urlMailProvider, e.getMessage());
		res = "";
	}
	return res;
}
private String getUrlAssigner() {
	
	String res = loadBalancer.getBaseUrl(assignerServiceName) + "/mail/assigner";
	LOG.debug("URL for getting assigner mail is {}", res);
	return res ;
}
}
