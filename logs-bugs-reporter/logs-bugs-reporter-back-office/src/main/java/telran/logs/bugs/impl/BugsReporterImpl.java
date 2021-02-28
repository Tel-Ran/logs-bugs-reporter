package telran.logs.bugs.impl;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import telran.logs.bugs.dto.*;
import telran.logs.bugs.interfaces.BugsReporter;
import telran.logs.bugs.jpa.entities.*;
import telran.logs.bugs.jpa.repo.*;
@Service
public class BugsReporterImpl implements BugsReporter {


public static final String CLOSING_DESCRIPTION = "\nClosing description:\n";
BugRepository bugRepository;
ArtifactRepository artifactRepository;
ProgrammerRepository programmerRepository;

public BugsReporterImpl(BugRepository bugRepository, ArtifactRepository artifactRepository,
		ProgrammerRepository programmerRepository) {
	this.bugRepository = bugRepository;
	this.artifactRepository = artifactRepository;
	this.programmerRepository = programmerRepository;
}
	@Override
	@Transactional
	public ProgrammerDto addProgrammer(ProgrammerDto programmerDto) {
		// FIXME exceptions handling and key duplication check
		programmerRepository
		.save(new Programmer(programmerDto.id, programmerDto.name, programmerDto.email));
		return programmerDto;
	}

	@Override
	public ArtifactDto addArtifact(ArtifactDto artifactDto) {
		// FIXME exceptions handling 
		Programmer programmer = programmerRepository.findById(artifactDto.programmerId).orElse(null);
		artifactRepository.save(new Artifact(artifactDto.artifactId,programmer ));
		return artifactDto;
	}

	@Override
	@Transactional
	public BugResponseDto openBug(BugDto bugDto) {
		//FIXME exceptions
		LocalDate dateOpen = bugDto.dateOpen != null ? bugDto.dateOpen : LocalDate.now();
		Bug bug = new Bug
				(bugDto.description, dateOpen, null, BugStatus.OPENNED,
						bugDto.seriousness,OpenningMethod.MANUAL, null);
		bugRepository.save(bug);
		return toBugResponseDto(bug);
	}

	private BugResponseDto toBugResponseDto(Bug bug) {
		
		Programmer programmer = bug.getProgrammer();
		long programmerId = programmer == null ? 0 : programmer.getId();
		return new BugResponseDto
				(bug.getId(), bug.getSeriousness(), bug.getDescription(),
						bug.getDateOpen(), programmerId, bug.getDateClose(),
						bug.getStatus(), bug.getOpenningMethod());
	}
	@Override
	@Transactional
	public BugResponseDto openAndAssignBug(BugAssignDto bugDto) {
		//FIXME exceptions
		Programmer programmer = programmerRepository.findById(bugDto.programmerId).orElse(null);
		//TODO exception in the case programmer is null
		LocalDate dateOpen = bugDto.dateOpen != null ? bugDto.dateOpen : LocalDate.now();
		Bug bug = 
				new Bug(bugDto.description, dateOpen, null, BugStatus.ASSIGNED,
						bugDto.seriousness, OpenningMethod.MANUAL, programmer);
		bug = bugRepository.save(bug);
		return toBugResponseDto(bug);
	}

	@Override
	@Transactional
	public void assignBug(AssignBugData assignData) {
		//FIXME exceptions
		Bug bug = bugRepository.findById(assignData.bugId).orElse(null);
		bug.setDescription(bug.getDescription() + BugsReporter.ASSIGNMENT_DESCRIPTION_TITLE +
		assignData.description);
		Programmer programmer = programmerRepository.findById(assignData.programmerId)
				.orElse(null);
		bug.setStatus(BugStatus.ASSIGNED);
		bug.setProgrammer(programmer);

	}

	@Override
	public List<BugResponseDto> getNonAssignedBugs() {
		
		List<Bug> bugs = bugRepository.findByStatus(BugStatus.OPENNED);
		return toListBugResponseDto(bugs );
	}

	@Override
	@Transactional
	public void closeBug(CloseBugData closeData) {
		//FIXME exceptions
		Bug bug = bugRepository.findById(closeData.bugId).orElse(null);
		LocalDate dateClose = closeData.dateClose == null ? LocalDate.now() : closeData.dateClose;
		bug.setStatus(BugStatus.CLOSED);
		bug.setDateClose(dateClose);
		bug.setDescription(bug.getDescription() + CLOSING_DESCRIPTION + closeData.description);
		
	}

	@Override
	public List<BugResponseDto> getUnClosedBugsMoreDuration(int days) {
		
		LocalDate dateOpen = LocalDate.now().minusDays(days);
		List<Bug> bugs = bugRepository.findByStatusNotAndDateOpenBefore(BugStatus.CLOSED, dateOpen );
		return toListBugResponseDto(bugs );
	}

	@Override
	public List<BugResponseDto> getBugsProgrammer(long programmerId) {
		List<Bug> bugs = bugRepository.findByProgrammerId(programmerId);
		return bugs.isEmpty() ? new LinkedList<>() : toListBugResponseDto(bugs);
	}

	private List<BugResponseDto> toListBugResponseDto(List<Bug> bugs) {
		return bugs.stream().map(this::toBugResponseDto).collect(Collectors.toList());
	}
	@Override
	public List<EmailBugsCount> getEmailBugsCounts() {
		
		List<EmailBugsCount> result = bugRepository.emailBugsCounts();
		return result ;
	}

	@Override
	public List<String> getProgrammersMostBugs(int nProgrammers) {
		
		return bugRepository.prgrammersMostBugs(nProgrammers);
	}

	@Override
	public List<String> getProgrammersLeastBugs(int nProgrammers) {
		
		return bugRepository.programmersLeastBugs(nProgrammers);
	}
	@Override
	public List<SeriousnessBugCount> getSeriousnessBugCounts() {
		
		
		return Arrays.stream(Seriousness.values()).map(s -> 
		new SeriousnessBugCount(s, bugRepository.countBySeriousness(s))
		).sorted((s1, s2) -> Long.compare(s2.getCount(), s1.getCount())) .collect(Collectors.toList());
	}
	@Override
	public List<Seriousness> getSeriousnessTypesWithMostBugs(int nTypes) {
		
		return bugRepository.seriousnessMostBugs(nTypes);
	}

}
