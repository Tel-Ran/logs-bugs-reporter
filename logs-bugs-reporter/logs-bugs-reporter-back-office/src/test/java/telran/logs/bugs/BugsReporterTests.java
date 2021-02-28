package telran.logs.bugs;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.validation.constraints.*;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import telran.logs.bugs.dto.*;
import telran.logs.bugs.impl.BugsReporterImpl;
import telran.logs.bugs.interfaces.BugsReporter;
import telran.logs.bugs.jpa.entities.Programmer;

import static telran.logs.bugs.api.BugsReporterApi.*;

@SpringBootTest
@AutoConfigureWebTestClient
@AutoConfigureDataJpa
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BugsReporterTests {
	static class EmailBugCountTest implements EmailBugsCount {
public EmailBugCountTest(String email, long count) {
			super();
			this.email = email;
			this.count = count;
		}

@Override
		public int hashCode() {
			return Objects.hash(count, email);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			EmailBugCountTest other = (EmailBugCountTest) obj;
			return count == other.count && Objects.equals(email, other.email);
		}

String email;
long count;
public EmailBugCountTest() {
}

		@Override
		public String getEmail() {
			return email;
		}

		@Override
		public long getCount() {
			
			return count;
		}
		
	}
	private static final @NotEmpty String DESCRIPTION = "Not working";
	private static final LocalDate DATE_OPEN = LocalDate.of(2020,12,1);
	private static final @Min(1) long PROGRAMMER_ID_VALUE_MOSHE = 123;
	private static final @Email String MOSHE_EMAIL ="moshe@gmail.com";
	private static final @Email String VASYA_EMAIL = "vasya@gmail.com";
	private static final String ARTIFACT_ID = "artifact123";
	private static final @Min(1) long PROGRAMMER_ID_VALUE_VASYA = 124;
	private static final @NotEmpty String TEST_CLOSE_DESCRIPTION = "closed by QA";
	private static final @NotEmpty String DESCRIPTION_CLOSED = DESCRIPTION
			+ BugsReporterImpl.CLOSING_DESCRIPTION + TEST_CLOSE_DESCRIPTION;
	private static final @Min(1) long BUG_CLOSE_ID_VALUE = 2;
	private static final int DAYS = 30;
	BugDto bugUnAssigned = new BugDto(Seriousness.BLOCKING, DESCRIPTION,
			DATE_OPEN);
	BugAssignDto bugAssigned2 = new BugAssignDto(Seriousness.BLOCKING, DESCRIPTION, DATE_OPEN, PROGRAMMER_ID_VALUE_MOSHE);
	BugAssignDto bugAssigned3 = new BugAssignDto(Seriousness.BLOCKING, DESCRIPTION, DATE_OPEN, PROGRAMMER_ID_VALUE_MOSHE);
	BugResponseDto expectedUnAssigned = new BugResponseDto(1, Seriousness.BLOCKING, DESCRIPTION,
			DATE_OPEN, 0, null, BugStatus.OPENNED, OpenningMethod.MANUAL);
	BugResponseDto expectedAssigned2 = new BugResponseDto(2, Seriousness.BLOCKING, DESCRIPTION,
			DATE_OPEN, PROGRAMMER_ID_VALUE_MOSHE, null, BugStatus.ASSIGNED, OpenningMethod.MANUAL);
	BugResponseDto expectedAssigned3 = new BugResponseDto(3, Seriousness.BLOCKING, DESCRIPTION,
			DATE_OPEN, PROGRAMMER_ID_VALUE_MOSHE, null, BugStatus.ASSIGNED, OpenningMethod.MANUAL);
	BugResponseDto expectedAssigned1 = new BugResponseDto(1, Seriousness.BLOCKING, DESCRIPTION + BugsReporter.ASSIGNMENT_DESCRIPTION_TITLE,
			DATE_OPEN, PROGRAMMER_ID_VALUE_MOSHE, null, BugStatus.ASSIGNED, OpenningMethod.MANUAL);
	List<BugResponseDto> expectedBugs123 = Arrays.asList(expectedAssigned1,
			expectedAssigned2, expectedAssigned3);
	BugResponseDto expectedClosed2 = new BugResponseDto(BUG_CLOSE_ID_VALUE, Seriousness.BLOCKING, DESCRIPTION_CLOSED,
			DATE_OPEN, PROGRAMMER_ID_VALUE_MOSHE, LocalDate.now(), BugStatus.CLOSED, OpenningMethod.MANUAL);
	List<EmailBugCountTest> expectedEmailCounts = Arrays.asList(new EmailBugCountTest(MOSHE_EMAIL, 3),
			new EmailBugCountTest(VASYA_EMAIL, 0));
	List<BugResponseDto> expectedListUnClosed = Arrays.asList(expectedAssigned1,expectedAssigned3);
	List<BugResponseDto> expectedBugsAfterColose = Arrays.asList(expectedAssigned1,
			expectedClosed2, expectedAssigned3);
	List<SeriousnessBugCount> seriousnessBugsDistribution = Arrays.asList(
			new SeriousnessBugCount(Seriousness.BLOCKING, 3),
			new SeriousnessBugCount(Seriousness.CRITICAL, 0),
			new SeriousnessBugCount(Seriousness.MINOR, 0),
			new SeriousnessBugCount(Seriousness.COSMETIC, 0)
			
			);
	List<Seriousness> seriousnessBugsMost = Arrays.asList(Seriousness.BLOCKING);
	
	
	@Autowired
WebTestClient testClient;
	@Test
	@Order(1)
	void addProgrammers() {
		ProgrammerDto programmer = new ProgrammerDto(PROGRAMMER_ID_VALUE_MOSHE,"Moshe", MOSHE_EMAIL);
		
		addProgrammerRequest(programmer);
		programmer = new ProgrammerDto(PROGRAMMER_ID_VALUE_VASYA, "Vasya", VASYA_EMAIL);
		addProgrammerRequest(programmer);
	}

	private void addProgrammerRequest(ProgrammerDto programmer) {
		testClient.post().uri(BUGS_PROGRAMMERS)
		.contentType(MediaType.APPLICATION_JSON).bodyValue(programmer)
		.exchange().expectStatus().isOk().expectBody(ProgrammerDto.class);
	}
	
	@Test
	@Order(2)
	void openBug() {
		testClient.post().uri(BUGS_OPEN)
		.contentType(MediaType.APPLICATION_JSON).bodyValue(bugUnAssigned).exchange().expectStatus().isOk()
		.expectBody(BugResponseDto.class).isEqualTo(expectedUnAssigned);
	}
	@Test
	@Order(3) 
	void openAndAssign() {
		
		openAssignRequest(bugAssigned2, expectedAssigned2);
		openAssignRequest(bugAssigned3, expectedAssigned3);
	}

	private void openAssignRequest(BugAssignDto bugAssignDto, BugResponseDto bugResponseDto) {
		testClient.post().uri(BUGS_OPEN_ASSIGN).bodyValue(bugAssignDto).exchange().expectStatus()
		.isOk().expectBody(BugResponseDto.class).isEqualTo(bugResponseDto);
	}
	@Test
	@Order(4)
	void assign() {
		testClient.put().uri(BUGS_ASSIGN).bodyValue(new AssignBugData(1, PROGRAMMER_ID_VALUE_MOSHE, ""))
		.exchange().expectStatus().isOk();
		
	}
	@Test
	@Order(5)
	void bugsProgrammersBeforeClose() {
		List<BugResponseDto> expectedList = expectedBugs123;
		bugsProgrammerTest(expectedList);
	}

	private void bugsProgrammerTest(List<BugResponseDto> expectedList) {
		String uriStr = BUGS_PROGRAMMERS + "?" + PROGRAMMER_ID + "=" + PROGRAMMER_ID_VALUE_MOSHE;
		getListBugs(expectedList, uriStr);
	}

	private void getListBugs(List<BugResponseDto> expectedList, String uriStr) {
		testClient.get().uri(uriStr).exchange().expectStatus().isOk()
		.expectBodyList(BugResponseDto.class).isEqualTo(expectedList);
	}
	@Test
	@Order(6)
	void closeBug2() {
		CloseBugData closeData = new CloseBugData(BUG_CLOSE_ID_VALUE, null, TEST_CLOSE_DESCRIPTION);
		testClient.put().uri(BUGS_CLOSE).contentType(MediaType.APPLICATION_JSON)
		.bodyValue(closeData).exchange().expectStatus().isOk();
	}
	@Test
	@Order(7)
	void unclosedBugsDuration() {
		getListBugs(expectedListUnClosed, BUGS_UNCLOSED + "?" + N_DAYS + "=" + DAYS);
	}
	@Test
	@Order(8)
	void bugsProgrammersAfterClosing() {
		bugsProgrammerTest(expectedBugsAfterColose);
	}
	@Test
	void bugsProgrammersNoProgrammerID() {
		testClient.get().uri(BUGS_PROGRAMMERS + "?" + PROGRAMMER_ID + "=" + 1000).exchange().expectStatus().isOk()
		.expectBodyList(BugResponseDto.class).isEqualTo(new LinkedList<>());
	}
	@Test
	void invalidOpenBug() {
		invalidPostRequest(BUGS_OPEN, new BugDto(Seriousness.BLOCKING, null, LocalDate.now()));
	}
	@Test
	void invalidAddProgrammer() {
		invalidPostRequest(BUGS_PROGRAMMERS, new Programmer(1, "Moshe", "kuku"));
	}
	@Test
	void invalidOpenAssignBug() {
		invalidPostRequest(BUGS_OPEN_ASSIGN, new BugAssignDto(Seriousness.BLOCKING,
				DESCRIPTION, DATE_OPEN, -20));
	}
	@Test
	void invalidAssignBug() {
		invalidPutRequest(BUGS_ASSIGN, new AssignBugData(0, PROGRAMMER_ID_VALUE_MOSHE, DESCRIPTION));
	}
	@Test
	void emailCounts() {
		testClient.get().uri(BUGS_PROGRAMMERS_COUNT).exchange().expectStatus().isOk()
		.expectBodyList(EmailBugCountTest.class).isEqualTo(expectedEmailCounts);
	}
	@Test
	void addArtifact() {
		ArtifactDto artifactDto = new ArtifactDto(ARTIFACT_ID, PROGRAMMER_ID_VALUE_MOSHE);
		testClient.post().uri(BUGS_ARTIFACTS).contentType(MediaType.APPLICATION_JSON)
		.bodyValue(artifactDto)
		.exchange().expectStatus().isOk().expectBody(ArtifactDto.class).isEqualTo(artifactDto);
		
	}
	@Test
	void invalidAddArtifact() {
		ArtifactDto invalidArtifact = new ArtifactDto("", PROGRAMMER_ID_VALUE_MOSHE);
		invalidPostRequest(BUGS_ARTIFACTS, invalidArtifact);
	}
	@Test
	void invalidCloseBug() {
		CloseBugData invalidCloseData = new CloseBugData(0, null, null);
		invalidPutRequest(BUGS_CLOSE, invalidCloseData);
	}
	@Test
	void programmersMostBugs() {
		
		getArrayProgrammers(BUGS_PROGRAMMERS_MOST, new String[] {MOSHE_EMAIL});
		
	}
	@Test
	void programmersLeastBugs() {
		
		getArrayProgrammers(BUGS_PROGRAMMERS_LEAST, new String[] {VASYA_EMAIL, MOSHE_EMAIL});
		
	}
	@Test

	private void getArrayProgrammers(String uriStr, String[] expected) {
		testClient.get().uri(uriStr).exchange().expectStatus().isOk()
		.expectBody(String[].class).isEqualTo(expected);
	}
	@Test
	void seriousnessDistribution() {
		testClient.get().uri(BUGS_SERIOUSNESS_COUNT).exchange().expectStatus().isOk()
		.expectBodyList(SeriousnessBugCount.class).isEqualTo(seriousnessBugsDistribution);
	}
	@Test
	void seriousnessMostBugs() {
		testClient.get().uri(BUGS_SERIOUSNESS_MOST).exchange().expectStatus().isOk()
		.expectBodyList(Seriousness.class).isEqualTo(seriousnessBugsMost);
	}

	private void invalidPostRequest(String uriStr, Object invalidObject) {
		testClient.post().uri(uriStr).contentType(MediaType.APPLICATION_JSON).bodyValue(invalidObject)
		.exchange().expectStatus().isBadRequest();
	}
	private void invalidPutRequest(String uriStr, Object invalidObject) {
		testClient.put().uri(uriStr).contentType(MediaType.APPLICATION_JSON).bodyValue(invalidObject)
		.exchange().expectStatus().isBadRequest();
	}
	
	
}

