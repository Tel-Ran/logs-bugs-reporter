package telran.logs.bugs.security.configuration;

import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;

import static telran.logs.bugs.api.BugsReporterApi.*;

@Configuration
public class SecurityConfiguration {

	public static final String DEVELOPER = "DEVELOPER";
	public static final String TESTER = "TESTER";
	public static final String ASSIGNER = "ASSIGNER";
	public static final String BUGS_REPORTER_BACK = "/bugs-reporter-back";
	public static final String LOGIN = "/login";
	public static final String PROJECT_OWNER = "PROJECT_OWNER";
	public static final String TEAM_LEAD = "TEAM_LEAD";
	public static final String LOGS_BACK = "/logs-back";
@Autowired
	UserDetailsRefreshService refreshService;
	
@Autowired
	ConcurrentHashMap<String, UserDetails> users;
@Autowired
	private ReactiveAuthenticationManager authenticator;
@Autowired
	private ServerSecurityContextRepository securityContext;
	
	@Bean
	MapReactiveUserDetailsService getMapDetails() {

		return new MapReactiveUserDetailsService(users);

	}

	@Bean
	ConcurrentHashMap<String, UserDetails> getUsersMap() {
		return new ConcurrentHashMap<>();
	}
	@Bean
	PasswordEncoder getPasswordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	SecurityWebFilterChain securityFiltersChain(ServerHttpSecurity httpSecurity) {
		SecurityWebFilterChain securityFiltersChain = httpSecurity.csrf().disable().httpBasic().disable().cors()
				.disable().authenticationManager(authenticator).securityContextRepository(securityContext)
				.authorizeExchange().pathMatchers(LOGIN).permitAll().and()
				.authorizeExchange().pathMatchers(LOGS_BACK + "/**").hasRole(DEVELOPER)
				.pathMatchers(BUGS_REPORTER_BACK + BUGS_OPEN, BUGS_REPORTER_BACK + BUGS_OPEN_ASSIGN)
				.hasAnyRole(DEVELOPER, TESTER, ASSIGNER).pathMatchers(BUGS_REPORTER_BACK + BUGS_ASSIGN)
				.hasRole(ASSIGNER).pathMatchers(BUGS_REPORTER_BACK + BUGS_CLOSE).hasRole(TESTER)
				.pathMatchers(HttpMethod.POST, BUGS_REPORTER_BACK + BUGS_PROGRAMMERS).hasRole(PROJECT_OWNER)
				.pathMatchers(HttpMethod.POST, BUGS_REPORTER_BACK + BUGS_ARTIFACTS).hasAnyRole(TEAM_LEAD, ASSIGNER)
				.anyExchange().authenticated().and().build();
		return securityFiltersChain;
	}

	@PostConstruct
	void updateMapUserDetails() throws InterruptedException {
		refreshService.start();

	}
}
