package telran.logs.bugs.security.authentication;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import reactor.core.publisher.Mono;
@Component
public class Authenticator implements ReactiveAuthenticationManager {
	static Logger LOG = LoggerFactory.getLogger(Authenticator.class);
@Autowired
	JwtUtil jwtUtil;
	
	@Override
	public Mono<Authentication> authenticate(Authentication authentication) {
		String authToken = authentication.getCredentials().toString();
		String[] roles;
		try {
			roles = jwtUtil.validateToken(authToken);
		} catch (MalformedJwtException e) {
			LOG.error("wrong token");
			return Mono.empty();
		}
		catch (ExpiredJwtException e) {
			LOG.error("expired token");
			return Mono.empty();
		}
		UsernamePasswordAuthenticationToken authenticationObj = 
				new UsernamePasswordAuthenticationToken(null, null,
						AuthorityUtils.createAuthorityList(roles));
		return Mono.just(authenticationObj);
	}

}
