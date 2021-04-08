package telran.logs.bugs;

import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.RestTemplate;

import reactor.core.publisher.Mono;
import static telran.logs.bugs.api.LogsInfoApi.*;
import static telran.logs.bugs.api.BugsReporterApi.*;
import static telran.logs.bugs.security.configuration.SecurityConfiguration.*;

import telran.logs.bugs.dto.AuthData;
import telran.logs.bugs.security.configuration.UserDetailsRefreshService;
import telran.logs.bugs.service.ProxyService;

@SpringBootTest
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LogsBugsGatewayTest {
	private static final String USERNAME = "developer";
	private static final String NOOP_PASSWORD = "{noop}developer";
	private static final String ROLE_DEVELOPER = "ROLE_DEVELOPER";
	private static final String PASSWORD = "developer";
	@Autowired
	WebTestClient testClient;
	
	@Autowired
	ConcurrentHashMap<String, UserDetails> users;
	@MockBean
	UserDetailsRefreshService refreshService;
	@MockBean
	ProxyService proxyService;
	static String jwt;
	AuthData correctAuthData = new AuthData(USERNAME, PASSWORD);
	AuthData wrongAuthData = new AuthData(USERNAME, "xxxx");
	@BeforeEach
	void setUp() {
		doNothing().when(refreshService).start();
		when(proxyService.proxyRun(any(), any(), any(HttpMethod.class)))
		.thenReturn(Mono.just(ResponseEntity.ok("ok".getBytes())));
		users.putIfAbsent(USERNAME, new User(USERNAME, NOOP_PASSWORD,
				AuthorityUtils.createAuthorityList(ROLE_DEVELOPER)));
	}
	/*********************************************************************/
	//login tests and getting JWT token
	@Test
	@Order(1)
	void loginNormal() {
		
		jwt = testClient.post().uri(LOGIN).bodyValue(correctAuthData )
		.exchange().expectStatus().isOk().returnResult(String.class)
		.getResponseBody().blockFirst();
		assertNotNull(jwt);
		assertEquals(3, jwt.split("\\.").length);
	}
	@Test
	void loginWrongAuthData() {
		testClient.post().uri(LOGIN).bodyValue(wrongAuthData )
		.exchange().expectStatus().isBadRequest();
	}
	/************************************************************************/
	/* Authentication tests */
	@Test
	@Order(2)
	void authenticationTestNormal() {
		String authToken = "Bearer " + jwt;
		testClient.get().uri(LOGS_BACK + LOGS)
		.header("Authorization", authToken)
		.exchange().expectStatus().isOk();
		
	}
	@Test
	@Order(3)
	void authenticationTestErrorNoToken() {
		
		testClient.get().uri(LOGS_BACK + LOGS)
		
		.exchange().expectStatus().isEqualTo(401);
	}
	@Test
	@Order(4)
	void authenticationTestErrorWrongToken() {
		
		String authToken = "Bearer " + jwt.substring(1);
		testClient.get().uri(LOGS_BACK + LOGS)
		.header("Authorization", authToken)
		.exchange().expectStatus().isEqualTo(401);
	}
	@Test
	@Order(5)
	void authenticationTestExpiredToken() throws InterruptedException {
		Thread.sleep(2000); //makes token be expired
		String authToken = "Bearer " + jwt;
		testClient.get().uri(LOGS_BACK + LOGS)
		.header("Authorization", authToken)
		.exchange().expectStatus().isEqualTo(401);
		
	}
	/*****************************************************************/
	/*******************************************************************/
	/* Authorization Tests Normal*/
	@Test
	
	
	@WithMockUser(roles = {DEVELOPER})
	void logsInfoAuthorizationNormal() {
		testClient.get().uri(LOGS_BACK + LOGS)
		.exchange().expectStatus().isOk();
	}
	@Test
	@WithMockUser(roles = {DEVELOPER})
	void bugsOpeninAuthorizationNormalDeveloper() {
		
		bugOpeningTest(200);
	}
	
	@Test
	@WithMockUser(roles = {TESTER})
	void bugsOpeninAuthorizationNormalTester() {
		bugOpeningTest(200);
	}
	@Test
	@WithMockUser(roles = {ASSIGNER})
	void bugsOpeninAuthorizationNormalAssigner() {
		bugOpeningTest(200);
	}
	@Test
	@WithMockUser(roles = {ASSIGNER})
	void bugsAssignAuthorizationNormalAssigner() {
		testClient.put().uri(BUGS_REPORTER_BACK + BUGS_ASSIGN).bodyValue("xxx")
		.exchange().expectStatus().isEqualTo(200);
	}
	@Test
	@WithMockUser(roles = {TESTER})
	void bugsCloseAuthorizationNormalAssigner() {
		testClient.put().uri(BUGS_REPORTER_BACK + BUGS_CLOSE).bodyValue("xxx")
		.exchange().expectStatus().isEqualTo(200);
	}
	@Test
	@WithMockUser(roles = {PROJECT_OWNER})
	void bugsAddProgrammerAuthorizationNormalProjectOwner() {
		testClient.post().uri(BUGS_REPORTER_BACK + BUGS_PROGRAMMERS).bodyValue("xxx")
		.exchange().expectStatus().isEqualTo(200);
	}
	@Test
	@WithMockUser(roles = {TEAM_LEAD})
	void bugsAddArtifactAuthorizationTeamLead() {
		testClient.post().uri(BUGS_REPORTER_BACK + BUGS_ARTIFACTS).bodyValue("xxx")
		.exchange().expectStatus().isEqualTo(200);
	}
	@Test
	@WithMockUser(roles = {ASSIGNER})
	void bugsAddArtifactAuthorizationAssigner() {
		testClient.post().uri(BUGS_REPORTER_BACK + BUGS_ARTIFACTS).bodyValue("xxx")
		.exchange().expectStatus().isEqualTo(200);
	}
	@Test
	@WithMockUser(roles = {"ANY_ROLE"})
	void bugsAnyRoleNormal() {
		testClient.get().uri(BUGS_REPORTER_BACK + BUGS_PROGRAMMERS_COUNT)
		.exchange().expectStatus().isEqualTo(200);
	}
	/************************************************************/
	/* Authorization Tests Error*/
	@Test
	
	
	@WithMockUser(roles = {TESTER})
	void logsInfoAuthorizationError() {
		testClient.get().uri(LOGS_BACK + LOGS)
		.exchange().expectStatus().isEqualTo(403);
	}
	@Test
	@WithMockUser(roles = {TEAM_LEAD})
	void bugsOpeninAuthorizationError() {
		bugOpeningTest(403);
	}
	@Test
	@WithMockUser(roles = {DEVELOPER})
	void bugsAssignAuthorizationError() {
		testClient.put().uri(BUGS_REPORTER_BACK + BUGS_ASSIGN).bodyValue("xxx")
		.exchange().expectStatus().isEqualTo(403);
	}
	@Test
	@WithMockUser(roles = {ASSIGNER})
	void bugsCloseAuthorizationError() {
		testClient.put().uri(BUGS_REPORTER_BACK + BUGS_CLOSE).bodyValue("xxx")
		.exchange().expectStatus().isEqualTo(403);
	}
	@Test
	@WithMockUser(roles = {TEAM_LEAD})
	void bugsAddProgrammerAuthorizationError() {
		testClient.post().uri(BUGS_REPORTER_BACK + BUGS_PROGRAMMERS).bodyValue("xxx")
		.exchange().expectStatus().isEqualTo(403);
	}
	@Test
	
	@WithMockUser(roles = {TESTER})
	void bugsAddArtifactError() {
		testClient.post().uri(BUGS_REPORTER_BACK + BUGS_ARTIFACTS).bodyValue("xxx")
		.exchange().expectStatus().isEqualTo(403);
	}
	
	/************************************************************************/
	
	private void bugOpeningTest(int status) {
		testClient.post().uri(BUGS_REPORTER_BACK + BUGS_OPEN).bodyValue("xxx")
		.exchange().expectStatus().isEqualTo(status);
		testClient.post().uri(BUGS_REPORTER_BACK + BUGS_OPEN_ASSIGN).bodyValue("xxx")
		.exchange().expectStatus().isEqualTo(status);
	}
	
	
}
