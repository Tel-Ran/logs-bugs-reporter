package telran.logs.bugs.security.authentication;

import java.util.*;

import javax.annotation.PostConstruct;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;

@Component
public class JwtUtil {
	static Logger LOG = LoggerFactory.getLogger(JwtUtil.class);
	//FIXME with secret in an environment
	@Value("${app-token-secret}")
String secret;
	@Value("${app-seconds-in-unit}")
	long secondsInUnit;
	@Value("${app-expiration-units}")
	long expirationPeriod;
	public String generateToken(String username, String[] roles) {
		Date current = new Date();
		HashMap<String, Object> claims = new HashMap<>();
		claims.put("roles", roles);
		return Jwts.builder().setClaims(claims).setSubject(username)
		.setExpiration(new Date(current.getTime() + expirationPeriod * secondsInUnit *1000))
		.setIssuedAt(current).signWith(SignatureAlgorithm.HS512, secret).compact();
	}
	public String[] validateToken(String token) {
		@SuppressWarnings("unchecked")
		List<String> listRoles = (List<String>) Jwts.parser().setSigningKey(secret)
				.parseClaimsJws(token).getBody().get("roles");
		return listRoles.toArray(new String[0]);
				
	}
	@PostConstruct
	void logForConfiguration() {
		LOG.debug("secondsInUnit: {}; expirationPeriod: {}",secondsInUnit,expirationPeriod);
	}
}
