package telran.logs.bugs.dto;

import java.util.Objects;

import javax.validation.constraints.*;

public class ArtifactDto {
public ArtifactDto(@NotEmpty String artifactId, @Min(1) long programmerId) {
		super();
		this.artifactId = artifactId;
		this.programmerId = programmerId;
	}
@NotEmpty
	public String artifactId;
@Min(1)
public long programmerId;
@Override
public int hashCode() {
	return Objects.hash(artifactId, programmerId);
}
@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	ArtifactDto other = (ArtifactDto) obj;
	return Objects.equals(artifactId, other.artifactId) && programmerId == other.programmerId;
}
}
