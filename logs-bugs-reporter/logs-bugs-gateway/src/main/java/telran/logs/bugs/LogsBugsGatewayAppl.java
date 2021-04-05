package telran.logs.bugs;



import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.webflux.ProxyExchange;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Mono;
import telran.logs.bugs.service.ProxyService;

@SpringBootApplication
@RestController
	public class LogsBugsGatewayAppl {
	
	@Autowired
	ProxyService proxyService;
static Logger LOG = LoggerFactory.getLogger(LogsBugsGatewayAppl.class);
	public static void main(String[] args) {
		SpringApplication.run(LogsBugsGatewayAppl.class, args);

	}
	
	@PostMapping("/**")
	public Mono<ResponseEntity<byte[]>> postRequestsProxy(ProxyExchange<byte[]> proxy,
			ServerHttpRequest request) {
		
		
		return proxyService.proxyRun(proxy, request, HttpMethod.POST);
		
	}
	@GetMapping("/**")
	public Mono<ResponseEntity<byte[]>> getRequestsProxy(ProxyExchange<byte[]> proxy,
			ServerHttpRequest request) {
		
		return proxyService.proxyRun(proxy, request, HttpMethod.GET);
		
	}
	@PutMapping("/**")
	public Mono<ResponseEntity<byte[]>> putRequestsProxy(ProxyExchange<byte[]> proxy,
			ServerHttpRequest request) {
		
		return proxyService.proxyRun(proxy, request, HttpMethod.PUT);
		
	}
	@DeleteMapping("/**")
	public Mono<ResponseEntity<byte[]>> deleteRequestsProxy(ProxyExchange<byte[]> proxy,
			ServerHttpRequest request) {
		
		return proxyService.proxyRun(proxy, request, HttpMethod.DELETE);
		
	}
	
	

}
