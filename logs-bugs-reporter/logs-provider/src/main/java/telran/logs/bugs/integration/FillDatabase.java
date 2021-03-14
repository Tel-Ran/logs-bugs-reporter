package telran.logs.bugs.integration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import telran.logs.bugs.entities.repo.*;
import telran.logs.bugs.jpa.entities.*;

@Component
public class FillDatabase {
	ProgrammerRepository programmerRepository;
	ArtifactRepository artifactRepository;
	public FillDatabase(ProgrammerRepository programmerRepository, ArtifactRepository artifactRepository) {
		this.programmerRepository = programmerRepository;
		this.artifactRepository = artifactRepository;
	}
	@Value("${app-integration-test-enable:true}")
boolean integrationTest;
	@Value("${app-programmer-names:Moshe,"
			+ "David,Avraham,Kseniya,Sergey,Yosef,"
			+ "Sara,Vasya,Asher,Yuri,Igor,Benyamin,"
			+ "Genady,Mariya,Sonya,Nikolay, Amir}")
	String names[];
	@Value("${app-mail-account:llogs.bugs.reporter}")
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
		List<Artifact> artifactsForSaving = new ArrayList<>();
		for (String artifactId: artifacts) {
			artifactsForSaving.add(new Artifact(artifactId, getProgrammer()));
		}
		for (int i = 1; i <= nArtifactsNofixed; i++) {
			artifactsForSaving.add(new Artifact(artifactBase + i,
					getProgrammer()));
		}
		artifactRepository.saveAll(artifactsForSaving);
		
	}
	private Programmer getProgrammer() {
		Programmer programmer =
				programmerRepository.findById(getRandomLong(1, names.length + 1)).orElse(null);
		return programmer;
	}
	private long getRandomLong(int min, int max) {
		return ThreadLocalRandom.current().nextLong(min, max);
	}
	int programmerId;
	private void fillProgrammers() {
		programmerId = 1;
		List<Programmer> programmersForSaving = 
				Arrays.stream(names).map(name -> new Programmer(programmerId, name,
						getEmail(programmerId++))).collect(Collectors.toList());
		programmerRepository.saveAll(programmersForSaving);
	
		}
		
	
	private String getEmail(long programmerId) {
		return mailAccount + "+" + programmerId + "@gmail.com";
	}
	
	
}
