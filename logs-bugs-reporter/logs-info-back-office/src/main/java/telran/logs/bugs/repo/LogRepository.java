package telran.logs.bugs.repo;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Flux;
import telran.logs.bugs.dto.ArtifactCount;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.dto.LogTypeCount;
import telran.logs.bugs.mongo.doc.LogDoc;

public interface LogRepository extends ReactiveMongoRepository<LogDoc, ObjectId>,LogStatistics {

	Flux<LogDto> findByLogType(LogType logType);

	Flux<LogDto> findByLogTypeNot(LogType logType);

	

	

}
