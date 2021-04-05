package telran.logs.bugs.security.configuration;

import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import telran.logs.bugs.discovery.LoadBalancer;
import telran.logs.bugs.dto.Account;


@Service
public class UserDetailsRefreshService extends Thread {
	static Logger LOG = LoggerFactory.getLogger(UserDetailsRefreshService.class);
	RestTemplate restTemplate = new RestTemplate();
	@Value("${app-accounting-username:user}")
	String username;
	@Value("${app-accounting-password:****}")
	String password;
	
	@Value("${app-refresh-timeout:30000}")
	long timeout;
	@Autowired
	LoadBalancer loadBalancer;
public UserDetailsRefreshService() {
		
		
		setDaemon(true);
	}
@Autowired
ConcurrentHashMap<String, UserDetails> users;
@Value("${app.accounts.provider.service.name:accounts-provider}")
private String serviceName;

@Override
public void run() {
	while (true) {
		
		fillUserDetails();
		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {
			
		}
	}
}
private void fillUserDetails() {
	Account[] accounts = getAccounts();
		
		 fillUsers(accounts);
}
private Account[] getAccounts() {
	HttpHeaders headers = new HttpHeaders();
	String authToken = getAuthToken();
	
	headers.add("Authorization", authToken);
	HttpEntity<String> requestEntity = new HttpEntity<>(headers);
	ResponseEntity<Account[]> response =
			restTemplate.exchange(getUrl(), HttpMethod.GET, requestEntity,
					Account[].class);
	Account[]accounts = response.getBody();
	LOG.debug("accounts: {}", Arrays.deepToString(accounts));
	return accounts;
}
private void fillUsers(Account[] accounts) {
	users.clear();
	Arrays.stream(accounts).map(a ->
	new User(a.username, a.password, getAuthorities(a)))
	.forEach(ud -> users.put(ud.getUsername(), ud));
	
}
private Collection<? extends GrantedAuthority> getAuthorities(Account account) {
String[] roles = Arrays.stream(account.roles).map(r -> "ROLE_" + r).toArray(String[]::new);
return AuthorityUtils.createAuthorityList(roles);
}
private String getUrl() {
	
	return loadBalancer.getBaseUrl(serviceName) + "/accounts";
}
private String getAuthToken() {
	String tokenText = String.format("%s:%s",username,password);
	return "Basic " + Base64.getEncoder().encodeToString(tokenText.getBytes());
}
}
