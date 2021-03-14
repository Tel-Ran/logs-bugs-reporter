package telran.logs.bugs.entities.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import telran.logs.bugs.jpa.entities.Artifact;

public interface ArtifactRepository extends JpaRepository<Artifact, String> {

}
