package telran.logs.bugs.mongo.documents;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import telran.logs.bugs.security.accounts.mongo.AccountDocument;


public interface AccountRepository extends MongoRepository<AccountDocument, String> {

	List<AccountDocument> findByExpirationTimestampGreaterThan(long l);

}
