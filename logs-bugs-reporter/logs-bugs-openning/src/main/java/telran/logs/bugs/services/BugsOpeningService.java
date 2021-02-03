package telran.logs.bugs.services;

import java.time.LocalDate;
import java.util.function.Consumer;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.jpa.entities.*;
import telran.logs.bugs.repo.ArtifactsRepo;
import telran.logs.bugs.repo.BugsRepo;

@Service
public class BugsOpeningService {
	static Logger LOG = LoggerFactory.getLogger(BugsOpeningService.class);
	@Autowired
ArtifactsRepo artifactsRepo;
	@Autowired
BugsRepo bugsRepo;
	@Bean
	Consumer<LogDto> getBugOpeningConsumer() {
		//consuming all log exceptions
		return this::bugOpening;
	}
	@Transactional
	void bugOpening(LogDto logException) {
		LOG.debug("Bugs Opening service has recieved log: {}", logException);
		String description = logException.logType + " " + logException.result;
		Programmer programmer = getProgrammer(logException.artifact);
		BugStatus bugStatus = programmer == null ? BugStatus.OPENNED : BugStatus.ASSIGNED ;
		Seriousness seriousness = getSeriousness(logException.logType);
		Bug bug = new Bug(description,
				LocalDate.now(), null, bugStatus, seriousness, OpenningMethod.AUTOMATIC,
				programmer);
		bugsRepo.save(bug);
		LOG.debug("Opening service has added bug with description: {},"
				+ " status: {}, seriousness: {}, assigned to {} ", description, bugStatus,
				seriousness, programmer == null ? "no assigned" : programmer.getName());
		
	}
	private Seriousness getSeriousness(LogType logType) {
		switch(logType) {
		case AUTHENTICATION_EXCEPTION: return Seriousness.BLOCKING;
		case AUTHORIZATION_EXCEPTION: case SERVER_EXCEPTION: return Seriousness.CRITICAL;
		default: return Seriousness.MINOR;
		}
	}
	private Programmer getProgrammer(String artifact) {
		Artifact artifactDb = artifactsRepo.findById(artifact).orElse(null);
		return artifactDb == null ? null : artifactDb.getProgrammer();
	}
}
