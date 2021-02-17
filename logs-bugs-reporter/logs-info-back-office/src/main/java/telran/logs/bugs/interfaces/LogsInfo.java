package telran.logs.bugs.interfaces;

import reactor.core.publisher.Flux;
import telran.logs.bugs.dto.*;

public interface LogsInfo {
Flux<LogDto> getAllLogs();
Flux<LogDto> getAllExceptions();
Flux<LogDto> getLogsType(LogType logType);
Flux<LogTypeCount> getLogTypeOccurrences();
Flux<LogType> getMostEncounteredExceptionTypes(int nExceptions);
Flux<ArtifactCount> getArtifactOccurrences();
Flux<String> getMostEncounteredArtifacts(int nArtifacts);

}
