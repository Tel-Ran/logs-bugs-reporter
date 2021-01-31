package telran.logs.bugs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import telran.logs.bugs.jpa.entities.Programmer;

@SpringBootTest
@AutoConfigureTestDatabase
public class OpenningBugsTest {
@Autowired
ProgrammersRepo programmers;
@Test
void primitiveTest() {
	programmers.save(new Programmer(123, "Moshe"));
	
}
}
