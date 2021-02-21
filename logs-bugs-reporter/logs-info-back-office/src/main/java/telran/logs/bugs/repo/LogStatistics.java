package telran.logs.bugs.repo;

import reactor.core.publisher.Flux;
import telran.logs.bugs.dto.*;

public interface LogStatistics {
	Flux<LogTypeCount>getLogTypeCounts();
	Flux<ArtifactCount> getArtifactCounts();
	Flux<ArtifactCount> getMostEncounteredArtifacts(int nArtifacts);
	Flux<LogTypeCount> getMostEncounteredExceptions(int nExceptions);
}
