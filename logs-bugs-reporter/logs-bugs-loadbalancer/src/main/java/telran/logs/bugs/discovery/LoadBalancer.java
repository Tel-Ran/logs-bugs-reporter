package telran.logs.bugs.discovery;

import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class LoadBalancer {
	static Logger LOG = LoggerFactory.getLogger(LoadBalancer.class);
	@Value("${app-internal-services-port:0}")
int internalServicesPort;
	@Value("${app-localhost}")
	boolean isLocalHost;
	@Value("${app-accounts-provider:accounts-provider}")
	String accountsProvider;
	@Value("${app-accounts-provider-port:1000}")
	int accountsProviderPort;
	@Value("${app-assigner-mail-provider:assigner-mail-provider}")
	String assignerMailProvider;
	@Value("${app-assigner-mail-provider-port:1001}")
	int assignerMailProviderPort;
	@Value("${app-email-provider:email-provider}")
	String emailProvider;
	@Value("${app-email-provider-port:1002}")
	int emailProviderPort;
	HashMap<String, Integer> servicePorts;
	
public String getBaseUrl(String serviceName) {
	String hostName = isLocalHost ? "localhost" : serviceName;
	int port = isLocalHost ? servicePorts.getOrDefault(serviceName, 0) : internalServicesPort;
	if (port == 0) {
		LOG.warn("assigned port is 0. Likely wrong URL will be received");
	} else {
		LOG.debug("assigned port is {}", port);
	}
	return String.format("http://%s:%d", hostName,port);
	
}
@PostConstruct
void fillServicePorts() {
	servicePorts = new HashMap<>();
	servicePorts.put(assignerMailProvider, assignerMailProviderPort);
	servicePorts.put(emailProvider, emailProviderPort);
	servicePorts.put(accountsProvider, accountsProviderPort);
}
}
