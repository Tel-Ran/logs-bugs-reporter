package telran.logs.bugs;

import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.webflux.ProxyExchange;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@SpringBootApplication
@RestController
public class LogsBugsGatewayAppl {
	@Value("${app-services-allowed:  bugs-reporter-back:8282, logs-back:8080}")
	List<String> allowedServices;
	HashMap<String, String> mapServices; //key - service name, value - base URL
	@Value("${app-localhost:false}")
	boolean isLocalhost;
static Logger LOG = LoggerFactory.getLogger(LogsBugsGatewayAppl.class);
	public static void main(String[] args) {
		SpringApplication.run(LogsBugsGatewayAppl.class, args);

	}
	@PostConstruct
	void fillMapServices() {
		mapServices = new HashMap<>();
		allowedServices.forEach(s -> {
			String[] serviceTokens = s.split(":");
			String serviceName = serviceTokens[0];
			String port = serviceTokens[1];
			String baseUrl = String.format("http://%s:%s", isLocalhost ?
					"localhost" : serviceName, port);
			mapServices.put(serviceName, baseUrl);
		});
		LOG.debug("isLocalhost: {}, mapServices: {}",isLocalhost, mapServices);
	}
	@PostMapping("/**")
	public Mono<ResponseEntity<byte[]>> postRequestsProxy(ProxyExchange<byte[]> proxy,
			ServerHttpRequest request) {
		String proxiedUri = getProxiedUri(request);
		if(proxiedUri == null) {
			return Mono.just(ResponseEntity.status(404).body("Service not found".getBytes()));
		}
		return proxy.uri(proxiedUri).post();
		
	}
	@GetMapping("/**")
	public Mono<ResponseEntity<byte[]>> getRequestsProxy(ProxyExchange<byte[]> proxy,
			ServerHttpRequest request) {
		String proxiedUri = getProxiedUri(request);
		if(proxiedUri == null) {
			return Mono.just(ResponseEntity.status(404).body("Service not found".getBytes()));
		}
		return proxy.uri(proxiedUri).get();
		
	}
	private String getProxiedUri(ServerHttpRequest request) {
		String uri = request.getURI().toString();
		
		LOG.debug("received request: {}",uri);
		String serviceName = uri.split("/+")[2];
		LOG.debug("service name is {}", serviceName);
		String res = mapServices.get(serviceName);
		if(res != null) {
			int indService = uri.indexOf(serviceName) + serviceName.length();
			res += uri.substring(indService);
			LOG.debug("resulted uri: {}",res);
			
		}
		return res;
	}
	

}
