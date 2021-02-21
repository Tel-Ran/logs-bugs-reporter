package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import telran.logs.bugs.dto.BugStatus;
import telran.logs.bugs.dto.OpenningMethod;
import telran.logs.bugs.dto.Seriousness;
import telran.logs.bugs.jpa.entities.*;

@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@ContextConfiguration(classes= {ArtifactsRepo.class, ProgrammersRepo.class, BugsRepo.class})
public class EntitiesTest {
@Autowired
ArtifactsRepo artifacts;
@Autowired
BugsRepo bugs;

@Autowired
ProgrammersRepo programmers;
@Test
void bugCreation () {
	Programmer programmer = new Programmer(123, "Moshe", "moshe@gmail.com");
	programmers.save(programmer);
	Artifact artifact = new Artifact("authentication", programmer);
	artifacts.save(artifact);
	Bug bug = new Bug("description", LocalDate.now(), null, BugStatus.ASSIGNED,
			Seriousness.MINOR, OpenningMethod.AUTOMATIC, programmer);
	bugs.save(bug);
	List<Bug> bugsRes = bugs.findAll();
	assertEquals(1, bugsRes.size());
	assertEquals(bug, bugsRes.get(0));
	
}
}
