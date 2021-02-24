package telran.logs.bugs.impl;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import telran.logs.bugs.dto.ArtifactDto;
import telran.logs.bugs.dto.AssignBugData;
import telran.logs.bugs.dto.BugAssignDto;
import telran.logs.bugs.dto.BugDto;
import telran.logs.bugs.dto.BugResponseDto;
import telran.logs.bugs.dto.BugStatus;
import telran.logs.bugs.dto.CloseBugData;
import telran.logs.bugs.dto.EmailBugsCount;
import telran.logs.bugs.dto.OpenningMethod;
import telran.logs.bugs.dto.ProgrammerDto;
import telran.logs.bugs.interfaces.BugsReporter;
import telran.logs.bugs.jpa.entities.Bug;
import telran.logs.bugs.jpa.entities.Programmer;
import telran.logs.bugs.jpa.repo.*;
@Service
public class BugsReporteImpl implements BugsReporter {


BugRepository bugRepository;
ArtifactRepository artifactRepository;
ProgrammerRepository programmerRepository;
@Autowired
public BugsReporteImpl(BugRepository bugRepository, ArtifactRepository artifactRepository,
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
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void closeBug(CloseBugData closeData) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<BugResponseDto> getUnClosedBugsMoreDuration(int days) {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getProgrammersMostBugs(int nProgrammers) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getProgrammersLeastBugs(int nProgrammers) {
		// TODO Auto-generated method stub
		return null;
	}

}
