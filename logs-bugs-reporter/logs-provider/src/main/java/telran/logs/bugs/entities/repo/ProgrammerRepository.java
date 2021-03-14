package telran.logs.bugs.entities.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import telran.logs.bugs.jpa.entities.Programmer;

public interface ProgrammerRepository extends JpaRepository<Programmer, Long> {

}
