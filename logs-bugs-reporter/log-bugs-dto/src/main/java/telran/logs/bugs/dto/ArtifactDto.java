package telran.logs.bugs.dto;

import javax.validation.constraints.*;

public class ArtifactDto {
@NotEmpty
	String artifactId;
@Min(1)
long programmerId;
}
