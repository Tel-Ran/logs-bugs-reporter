package telran.logs.bugs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("telran.logs.bugs.jpa.entities")
public class BugsOpenningAppl {

	public static void main(String[] args) {
		SpringApplication.run(BugsOpenningAppl.class, args);

	}

}
