package telran.logs.bugs.security.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
@Component
public class CustomSecurityContext implements ServerSecurityContextRepository {
@Autowired
	Authenticator authenticator;
	@Override
	public Mono<Void> save(ServerWebExchange exchange,
			SecurityContext context) {
		//unsupported method
		return null;
	}

	@Override
	public Mono<SecurityContext> load(ServerWebExchange exchange) {
		String authHeader = exchange.getRequest()
				.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
	    if(authHeader == null || !authHeader.startsWith("Bearer"))
	    	return Mono.empty(); //no issue if security configuration contains permitAll
	    String authToken = authHeader.substring(7);
	    Mono<SecurityContext> result =
	    	authenticator.authenticate(new UsernamePasswordAuthenticationToken(authToken, authToken))
	    	.map(SecurityContextImpl::new);
		return result;
	}

}
