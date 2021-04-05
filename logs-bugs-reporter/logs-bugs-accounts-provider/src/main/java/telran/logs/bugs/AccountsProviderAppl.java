package telran.logs.bugs;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import telran.logs.bugs.mongo.documents.AccountRepository;
import telran.logs.bugs.dto.*;
import telran.logs.bugs.security.accounts.mongo.AccountDocument;

@SpringBootApplication
@RestController
public class AccountsProviderAppl {
	
	@Autowired
AccountRepository accountRepository;
	public static void main(String[] args) {
		SpringApplication.run(AccountsProviderAppl.class, args);

	}
	@GetMapping("/accounts")
	List<Account> getAccounts() {
		List<AccountDocument> listAccountDocs =
				accountRepository.findByExpirationTimestampGreaterThan(System.currentTimeMillis() / 1000);
		List<Account> res = toAccounts(listAccountDocs );
		return res ;
	}
	private List<Account> toAccounts(List<AccountDocument> listAccountDocs) {
		
		return listAccountDocs.stream()
				.map(ad -> new Account(ad.getUsername(), ad.getPassword(), ad.getRoles()))
				.collect(Collectors.toList());
	}

}
