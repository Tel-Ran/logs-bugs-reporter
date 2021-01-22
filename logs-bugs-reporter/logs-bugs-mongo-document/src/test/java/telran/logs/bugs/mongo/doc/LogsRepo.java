package telran.logs.bugs.mongo.doc;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LogsRepo extends MongoRepository<LogDoc, ObjectId> {

}


