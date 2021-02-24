package telran.logs.bugs.dto;

import java.util.Objects;

import javax.validation.constraints.*;

public class ProgrammerDto {
	public ProgrammerDto(@Min(1) long id, @NotEmpty String name, @Email String email) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
	}
	@Min(1)
public long id;
	@NotEmpty
public String name;
	@Email
public String email;
	@Override
	public int hashCode() {
		return Objects.hash(email, id, name);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProgrammerDto other = (ProgrammerDto) obj;
		return Objects.equals(email, other.email) && id == other.id && Objects.equals(name, other.name);
	}
}
