package telran.logs.bugs.dto;

import java.time.LocalDate;
import java.util.Objects;

import javax.validation.constraints.*;

public class BugAssignDto extends BugDto {
public BugAssignDto(@NotNull Seriousness seriousness, @NotEmpty String description, LocalDate dateOpen,
			@Min(1) long programmerId) {
		super(seriousness, description, dateOpen);
		this.programmerId = programmerId;
	}

@Min(1)
	public long programmerId;

@Override
public int hashCode() {
	final int prime = 31;
	int result = super.hashCode();
	result = prime * result + Objects.hash(programmerId);
	return result;
}

@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (!super.equals(obj))
		return false;
	if (getClass() != obj.getClass())
		return false;
	BugAssignDto other = (BugAssignDto) obj;
	return programmerId == other.programmerId;
}
}
