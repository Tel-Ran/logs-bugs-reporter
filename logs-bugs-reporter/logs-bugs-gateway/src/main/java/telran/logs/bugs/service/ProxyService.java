package telran.logs.bugs.service;


import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.webflux.ProxyExchange;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import telran.logs.bugs.LogsBugsGatewayAppl;

@Service
public class ProxyService {
	static Logger LOG = LoggerFactory.getLogger(LogsBugsGatewayAppl.class);
	@Value("${app-services-allowed:  bugs-reporter-back:8282, logs-back:8080}")
	List<String> allowedServices;
	
	HashMap<String, String> mapServices; //key - service name, value - base URL
	@Value("${app-localhost:false}")
	boolean isLocalhost;
	private Mono<ResponseEntity<byte[]>> proxyPerform(ProxyExchange<byte[]> proxy, String proxiedUri, HttpMethod method) {
		ProxyExchange<byte[]> proxyExchange = proxy.uri(proxiedUri);
		switch(method) {
		case POST: 
			return proxyExchange.post();
		case GET: return proxyExchange.get();
		case PUT: return proxyExchange.put();
		case DELETE: return proxyExchange.delete();
		
		default: return Mono.just(ResponseEntity.status(500).body("unsupported proxy operation".getBytes()));
		}
	}

	public Mono<ResponseEntity<byte[]>> proxyRun(ProxyExchange<byte[]> proxy, ServerHttpRequest request,
			HttpMethod method) {
		String proxiedUri = getProxiedUri(request);
		if(proxiedUri == null) {
			return Mono.just(ResponseEntity.status(404).body("Service not found".getBytes()));
		}
		return proxyPerform(proxy, proxiedUri, method);
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

}
