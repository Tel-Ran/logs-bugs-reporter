package telran.logs.bugs.mongo.doc;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface LogsRepo extends ReactiveMongoRepository<LogDoc, ObjectId> {

}


