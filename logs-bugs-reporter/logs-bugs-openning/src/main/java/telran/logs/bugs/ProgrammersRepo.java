package telran.logs.bugs;

import org.springframework.data.jpa.repository.JpaRepository;

import telran.logs.bugs.jpa.entities.Programmer;

public interface ProgrammersRepo extends JpaRepository<Programmer, Long> {

}
