package telran.logs.bugs.interfaces;

import java.util.List;

import telran.logs.bugs.dto.*;

public interface BugsReporter {
ProgrammerDto addProgrammer(ProgrammerDto programmerDto);
ArtifactDto addArtifact(ArtifactDto artifactDto);
BugResponseDto openBug(BugDto bugDto);
BugResponseDto openAndAssignBug(BugAssignDto bugDto);
void assignBug(AssignBugData assignData);
List<BugResponseDto> getNonAssignedBugs();
void closeBug(CloseBugData closeData);
List<BugResponseDto> getUnClosedBugsMoreDuration(int days);
List<BugResponseDto> getBugsProgrammer(long programmerId);
List<EmailBugsCount> getEmailBugsCounts();
List<String> getProgrammersMostBugs(int nProgrammers);
List<String> getProgrammersLeastBugs(int nProgrammers);

}
