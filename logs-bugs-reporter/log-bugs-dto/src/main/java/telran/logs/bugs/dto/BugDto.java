package telran.logs.bugs.dto;

import java.time.LocalDate;
import java.util.Objects;

import javax.validation.constraints.*;

public class BugDto {
	public BugDto(@NotNull Seriousness seriousness, @NotEmpty String description, LocalDate dateOpen) {
		super();
		this.seriousness = seriousness;
		this.description = description;
		this.dateOpen = dateOpen;
	}

	@NotNull
public Seriousness seriousness;
	@NotEmpty
	public String description;
	
	public LocalDate dateOpen;

	@Override
	public int hashCode() {
		return Objects.hash(dateOpen, description, seriousness);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BugDto other = (BugDto) obj;
		return Objects.equals(dateOpen, other.dateOpen) && Objects.equals(description, other.description)
				&& seriousness == other.seriousness;
	}
}
