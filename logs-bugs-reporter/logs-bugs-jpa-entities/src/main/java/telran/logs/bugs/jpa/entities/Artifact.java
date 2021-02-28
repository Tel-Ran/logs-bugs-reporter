package telran.logs.bugs.jpa.entities;
import javax.persistence.*;
@Entity
@Table(name="artifacts", indexes= {@Index(columnList = "programmer_id")})
public class Artifact {
	@Id
	@Column(name="artifact_id")
	String artifactId;
	@ManyToOne
	@JoinColumn(name = "programmer_id", nullable = false)
	Programmer programmer;
	public Artifact() {
	}
	public Artifact(String artifactId, Programmer programmer) {
		this.artifactId = artifactId;
		this.programmer = programmer;
	}
	public String getArtifactId() {
		return artifactId;
	}
	public Programmer getProgrammer() {
		return programmer;
	}
	
	

}

