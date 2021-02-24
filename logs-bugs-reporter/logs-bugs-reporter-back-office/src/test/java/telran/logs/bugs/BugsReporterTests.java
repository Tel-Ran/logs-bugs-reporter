package telran.logs.bugs;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import javax.validation.constraints.*;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import telran.logs.bugs.dto.*;
import telran.logs.bugs.interfaces.BugsReporter;
import telran.logs.bugs.jpa.entities.Programmer;

import static telran.logs.bugs.api.BugsReporterApi.*;

@SpringBootTest
@AutoConfigureWebTestClient
@AutoConfigureDataJpa
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BugsReporterTests {
	private static final @NotEmpty String DESCRIPTION = "Not working";
	private static final LocalDate DATE_OPEN = LocalDate.of(2020,12,1);
	private static final @Min(1) long PROGRAMMER_ID_VALUE = 123;
	private static final @Email String EMAIL ="moshe@gmail.com";
	BugDto bugUnAssigned = new BugDto(Seriousness.BLOCKING, DESCRIPTION,
			DATE_OPEN);
	BugDto bugAssigned2 = new BugAssignDto(Seriousness.BLOCKING, DESCRIPTION, DATE_OPEN, PROGRAMMER_ID_VALUE);
	BugDto bugAssigned3 = new BugAssignDto(Seriousness.BLOCKING, DESCRIPTION, DATE_OPEN, PROGRAMMER_ID_VALUE);
	BugResponseDto expectedUnAssigned = new BugResponseDto(1, Seriousness.BLOCKING, DESCRIPTION,
			DATE_OPEN, 0, null, BugStatus.OPENNED, OpenningMethod.MANUAL);
	BugResponseDto expectedAssigned2 = new BugResponseDto(2, Seriousness.BLOCKING, DESCRIPTION,
			DATE_OPEN, PROGRAMMER_ID_VALUE, null, BugStatus.ASSIGNED, OpenningMethod.MANUAL);
	BugResponseDto expectedAssigned3 = new BugResponseDto(3, Seriousness.BLOCKING, DESCRIPTION,
			DATE_OPEN, PROGRAMMER_ID_VALUE, null, BugStatus.ASSIGNED, OpenningMethod.MANUAL);
	BugResponseDto expectedAssigned1 = new BugResponseDto(1, Seriousness.BLOCKING, DESCRIPTION + BugsReporter.ASSIGNMENT_DESCRIPTION_TITLE,
			DATE_OPEN, PROGRAMMER_ID_VALUE, null, BugStatus.ASSIGNED, OpenningMethod.MANUAL);
	List<BugResponseDto> expectedBugs123 = Arrays.asList(expectedAssigned1,
			expectedAssigned2, expectedAssigned3);
	@Autowired
WebTestClient testClient;
	@Test
	@Order(1)
	void addProgrammers() {
		ProgrammerDto programmer = new ProgrammerDto(PROGRAMMER_ID_VALUE,"Moshe", EMAIL);
		
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
		
		testClient.post().uri(BUGS_OPEN_ASSIGN).bodyValue(bugAssigned2).exchange().expectStatus()
		.isOk().expectBody(BugResponseDto.class).isEqualTo(expectedAssigned2);
		testClient.post().uri(BUGS_OPEN_ASSIGN).bodyValue(bugAssigned3).exchange().expectStatus()
		.isOk().expectBody(BugResponseDto.class).isEqualTo(expectedAssigned3);
	}
	@Test
	@Order(4)
	void assign() {
		testClient.put().uri(BUGS_ASSIGN).bodyValue(new AssignBugData(1, PROGRAMMER_ID_VALUE, ""))
		.exchange().expectStatus().isOk();
		
	}
	@Test
	@Order(5)
	void bugsProgrammers() {
		testClient.get().uri(BUGS_PROGRAMMERS + "?" + PROGRAMMER_ID + "=" + PROGRAMMER_ID_VALUE).exchange().expectStatus().isOk()
		.expectBodyList(BugResponseDto.class).isEqualTo(expectedBugs123);
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
		invalidPutRequest(BUGS_ASSIGN, new AssignBugData(0, PROGRAMMER_ID_VALUE, DESCRIPTION));
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

