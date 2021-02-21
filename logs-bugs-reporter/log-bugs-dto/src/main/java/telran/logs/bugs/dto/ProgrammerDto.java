package telran.logs.bugs.dto;

import javax.validation.constraints.*;

public class ProgrammerDto {
	@Min(1)
public long id;
	@NotEmpty
public String name;
	@Email
public String email;
}
