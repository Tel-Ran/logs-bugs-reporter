package telran.logs.bugs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import telran.logs.bugs.jpa.entities.Artifact;
import telran.logs.bugs.repo.ArtifactRepository;

@SpringBootApplication
@RestController
public class EmailProviderAppl {
	@Autowired
ArtifactRepository artifactRepository;
	
	public static void main(String[] args) {
		SpringApplication.run(EmailProviderAppl.class, args);

	}
	@GetMapping("/email/{artifact}")
	String getEmail(@PathVariable(name="artifact") String artifact) {
		Artifact artifactEntity = artifactRepository.findById(artifact).orElse(null);
		return artifactEntity == null ? "" : artifactEntity.getProgrammer().getEmail();
		
	}

}
