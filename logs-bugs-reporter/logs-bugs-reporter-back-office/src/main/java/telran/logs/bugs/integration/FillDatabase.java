package telran.logs.bugs.integration;

import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import telran.logs.bugs.dto.ArtifactDto;
import telran.logs.bugs.dto.ProgrammerDto;
import telran.logs.bugs.interfaces.BugsReporter;

@Component
public class FillDatabase {
	@Autowired
	BugsReporter reporterService;
	@Value("${app-integration-test-enable:false}")
boolean integrationTest;
	@Value("${app-programmer-names:Moshe,"
			+ "David,Avraham,Kseniya,Sergey,Yosef,"
			+ "Sara,Vasya,Asher,Yuri,Igor,Benyamin,"
			+ "Genady,Mariya,Sonya,Nikolay, Amir}")
	String names[];
	@Value("${app-mail-account:xxx}")
	String mailAccount;
	@Value("${app-fixed-artifacts:authentication,authrization}")
	String []artifacts;
	@Value("${app-no-fixed-artifact-base:class}")
	String artifactBase;
	@Value("${app-no-fixed-artifact-amount:20}")
	int nArtifactsNofixed;
	@PostConstruct
	void fillDB() {
		if (integrationTest) {
			fillProgrammers();
			fillArtifacts();
		}
	}
	private void fillArtifacts() {
		for (String artifactId: artifacts) {
			reporterService.addArtifact(new ArtifactDto(artifactId,
					ThreadLocalRandom.current().nextLong(1, names.length + 1)));
		}
		for (int i = 1; i <= nArtifactsNofixed; i++) {
			reporterService.addArtifact(new ArtifactDto(artifactBase + i,
					ThreadLocalRandom.current().nextLong(1, names.length + 1)));
		}
		
	}
	private void fillProgrammers() {
		for (int i = 0; i < names.length; i++) {
			long programmerId = i + 1;
			reporterService.addProgrammer(new ProgrammerDto(programmerId, names[i],
					getEmail(programmerId)));
		}
		
	}
	private String getEmail(long programmerId) {
		return mailAccount + "+" + programmerId + "@gmail.com";
	}
	
	
}
